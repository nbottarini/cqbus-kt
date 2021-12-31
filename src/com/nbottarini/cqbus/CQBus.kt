package com.nbottarini.cqbus

import com.nbottarini.cqbus.requests.InternalRequest
import com.nbottarini.cqbus.requests.Request
import com.nbottarini.cqbus.requests.handlers.ContextAwareRequestHandler
import com.nbottarini.cqbus.requests.handlers.RequestHandler

class CQBus {
    private val handlers = mutableMapOf<Class<*>, () -> RequestHandler<*, *>>()
    private val contextAwareHandlers = mutableMapOf<Class<*>, () -> ContextAwareRequestHandler<*, *>>()
    private val middlewares = mutableListOf<Middleware>()
    var internalRequestsEnabled = false

    fun <T: Request<R>, R> registerHandler(requestType: Class<T>, handlerFactory: () -> RequestHandler<T, R>) {
        handlers[requestType] = handlerFactory
    }

    inline fun <reified T: Request<R>, R> registerHandler(noinline handlerFactory: () -> RequestHandler<T, R>) {
        registerHandler(T::class.java, handlerFactory)
    }

    fun <T: Request<R>, R> registerContextAwareHandler(requestType: Class<T>, handlerFactory: () -> ContextAwareRequestHandler<T, R>) {
        contextAwareHandlers[requestType] = handlerFactory
    }

    inline fun <reified T: Request<R>, R> registerContextAwareHandler(noinline handlerFactory: () -> ContextAwareRequestHandler<T, R>) {
        registerContextAwareHandler(T::class.java, handlerFactory)
    }

    fun <T: Request<R>, R> execute(request: T, context: ExecutionContext = ExecutionContext()): R {
        failIfInternalRequest(request.javaClass)
        var execute = handlerExecuteFunc(request, context)
        execute = applyMiddlewares(execute, context)
        return execute(request)
    }

    private fun failIfInternalRequest(clazz: Class<*>) {
        if (!canAccessRequest(clazz)) throw CannotAccessInternalRequestError(clazz.canonicalName)
    }

    private fun <R, T: Request<R>> applyMiddlewares(execute: (T) -> R, context: ExecutionContext): (T) -> R {
        var newExecute = execute
        middlewares.forEach { middleware ->
            val previousFunc = newExecute
            newExecute = { middleware.execute(it, previousFunc, context) }
        }
        return newExecute
    }

    private fun <T: Request<R>, R> handlerExecuteFunc(request: T, context: ExecutionContext): (T) -> R {
        var executeFunc: ((T) -> R)? = null
        val requestClass = request.javaClass
        if (handlers.containsKey(requestClass)) {
            executeFunc = regularHandlerExecuteFunc(requestClass, context)
        }
        if (contextAwareHandlers.containsKey(requestClass)) {
            executeFunc = contextAwareHandlerExecuteFunc(requestClass, context)
        }
        if (executeFunc == null) throw RequestHandlerNotRegisteredError(requestClass.canonicalName)
        return executeFunc
    }

    @Suppress("UNCHECKED_CAST")
    private fun <R, T: Request<R>> regularHandlerExecuteFunc(requestClass: Class<T>, context: ExecutionContext): (T) -> R = {
        val handler = handlers[requestClass]!!.invoke() as RequestHandler<T, R>
        handler.execute(it, context.identity)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <R, T: Request<R>> contextAwareHandlerExecuteFunc(requestClass: Class<T>, context: ExecutionContext): (T) -> R = {
        val handler = contextAwareHandlers[requestClass]!!.invoke() as ContextAwareRequestHandler<T, R>
        handler.execute(it, context)
    }

    private fun canAccessRequest(clazz: Class<*>) = internalRequestsEnabled || !isInternalRequest(clazz)

    private fun isInternalRequest(clazz: Class<*>) = clazz.isAnnotationPresent(InternalRequest::class.java)

    fun registerMiddleware(middleware: Middleware) {
        middlewares.add(middleware)
    }
}
