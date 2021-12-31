package com.nbottarini.cqbus.requests.handlers

import com.nbottarini.cqbus.identity.Identity
import com.nbottarini.cqbus.requests.Request

fun interface RequestHandler<TRequest: Request<TResponse>, TResponse> {
    fun execute(request: TRequest, identity: Identity): TResponse
}
