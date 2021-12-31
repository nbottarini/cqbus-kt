package com.nbottarini.cqbus.requests.handlers

import com.nbottarini.cqbus.ExecutionContext
import com.nbottarini.cqbus.requests.Request

fun interface ContextAwareRequestHandler<TRequest: Request<TResponse>, TResponse> {
    fun execute(request: TRequest, context: ExecutionContext): TResponse
}
