package com.nbottarini.asimov.cqbus.requests.handlers

import com.nbottarini.asimov.cqbus.identity.AnonymousIdentity
import com.nbottarini.asimov.cqbus.identity.Identity
import com.nbottarini.asimov.cqbus.requests.Request

fun interface RequestHandler<TRequest: Request<TResponse>, TResponse> {
    fun execute(request: TRequest): TResponse = execute(request, AnonymousIdentity())
    fun execute(request: TRequest, identity: Identity): TResponse
}
