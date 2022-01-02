package com.nbottarini.asimov.cqbus.requests.handlers

import com.nbottarini.asimov.cqbus.ExecutionContext
import com.nbottarini.asimov.cqbus.requests.Request

fun interface ContextAwareRequestHandler<TRequest: Request<TResponse>, TResponse> {
    fun execute(request: TRequest, context: ExecutionContext): TResponse
}
