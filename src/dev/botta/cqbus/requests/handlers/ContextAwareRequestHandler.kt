package dev.botta.cqbus.requests.handlers

import dev.botta.cqbus.ExecutionContext
import dev.botta.cqbus.requests.Request

fun interface ContextAwareRequestHandler<TRequest: Request<TResponse>, TResponse> {
    suspend fun execute(request: TRequest, context: ExecutionContext): TResponse
}
