@file:Suppress("ClassName")

package dev.botta.cqbus

import dev.botta.cqbus.identity.Identity
import dev.botta.cqbus.requests.Command
import dev.botta.cqbus.requests.InternalRequest
import dev.botta.cqbus.requests.PureCommand
import dev.botta.cqbus.requests.Request
import dev.botta.cqbus.requests.handlers.ContextAwareRequestHandler
import dev.botta.cqbus.requests.handlers.RequestHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class CQBusTest {
    @Test
    fun `executes request with registered handler and returns result`() {
        cqBus.registerHandler {
            RequestHandler<CreateFullName, String> {
                request, _ -> request.firstName + " " + request.lastName
            }
        }

        val result = cqBus.execute(CreateFullName("John", "Doe"))

        assertThat(result).isEqualTo("John Doe")
    }

    @Test
    fun `context-aware handlers can receive data via execution context`() {
        var receivedValue = ""
        cqBus.registerContextAwareHandler {
            ContextAwareRequestHandler<MyCommand, Unit> { _, context ->
                receivedValue = context["sample-key"] as String
            }
        }

        cqBus.execute(MyCommand(), ExecutionContext().with("sample-key", "some-value"))

        assertThat(receivedValue).isEqualTo("some-value")
    }

    @Test
    fun `request handlers receive the identity executing the request`() {
        var receivedIdentity: Identity? = null
        val alice = UserIdentity("Alice")
        cqBus.registerHandler {
            RequestHandler<MyCommand, Unit> { _, identity -> receivedIdentity = identity }
        }

        cqBus.execute(MyCommand(), ExecutionContext().withIdentity(alice))

        assertThat(receivedIdentity).isEqualTo(alice)
    }

    @Test
    fun `fails if request handler is not registered`() {
        assertThrows<RequestHandlerNotRegisteredError> { cqBus.execute(MyCommand()) }
    }

    @Nested
    inner class middlewares {
        @Test
        fun `executes middleware`() {
            cqBus.registerHandler { CreateFullNameLoggingHandler(log) }
            cqBus.registerMiddleware(LoggingMiddleware(log))

            cqBus.execute(CreateFullName("John", "Doe"))

            assertThat(log).containsExactly("before", "John Doe", "after")
        }

        @Test
        fun `executes multiple middlewares in reverse registration order`() {
            cqBus.registerHandler { CreateFullNameLoggingHandler(log) }
            cqBus.registerMiddleware(LoggingMiddleware(log, suffix = "1"))
            cqBus.registerMiddleware(LoggingMiddleware(log, suffix = "2"))

            cqBus.execute(CreateFullName("John", "Doe"))

            assertThat(log).containsExactly("before2", "before1", "John Doe", "after1", "after2")
        }

        @Test
        fun `middlewares can pass data between each other via the execution context`() {
            var receivedValue = ""
            cqBus.registerHandler { CreateFullNameLoggingHandler(log) }
            cqBus.registerMiddleware(SimpleMiddleware { context ->
                receivedValue = context["sample-key"] as String
            })
            cqBus.registerMiddleware(SimpleMiddleware { context ->
                context["sample-key"] = "some-value"
            })

            cqBus.execute(CreateFullName("John", "Doe"))

            assertThat(receivedValue).isEqualTo("some-value")
        }
    }

    @Nested
    inner class `internal requests` {
        @Test
        fun `fails if request is internal and internal requests are not enabled`() {
            cqBus.internalRequestsEnabled = false
            cqBus.registerHandler { MyInternalCommandHandler() }

            assertThrows<CannotAccessInternalRequestError> { cqBus.execute(MyInternalCommand()) }
        }

        @Test
        fun `don't fail if handler is internal and internal requests are enabled`() {
            cqBus.internalRequestsEnabled = true
            cqBus.registerHandler { MyInternalCommandHandler() }

            assertDoesNotThrow { cqBus.execute(MyInternalCommand()) }
        }
    }

    private var log = mutableListOf<String>()
    private val cqBus = CQBus()
}

class CreateFullName(val firstName: String, val lastName: String): Command<String>

class MyCommand: PureCommand

@InternalRequest
class MyInternalCommand: Command<Unit>

class MyInternalCommandHandler: RequestHandler<MyInternalCommand, Unit> {
    override fun execute(request: MyInternalCommand, identity: Identity) {}
}

class CreateFullNameLoggingHandler(private val log: MutableList<String>):
    RequestHandler<CreateFullName, String> {
    override fun execute(request: CreateFullName, identity: Identity): String {
        val result = request.firstName + " " + request.lastName
        log.add(result)
        return result
    }
}

class LoggingMiddleware(private val log: MutableList<String>, private val suffix: String = ""): Middleware {
    override fun <T: Request<R>, R> execute(request: T, next: (T) -> R, context: ExecutionContext): R {
        log.add("before$suffix")
        val result = next(request)
        log.add("after$suffix")
        return result
    }
}

class SimpleMiddleware(private val block: (ExecutionContext) -> Unit): Middleware {
    override fun <T: Request<R>, R> execute(request: T, next: (T) -> R, context: ExecutionContext): R {
        block(context)
        return next(request)
    }
}

class UserIdentity(override val name: String): Identity {
    override val isAuthenticated = true
    override val authenticationType: String? = null
    override val roles = listOf<String>()
    override val properties = mapOf<String, Any>()
}
