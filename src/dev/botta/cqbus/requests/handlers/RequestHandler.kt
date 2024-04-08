package dev.botta.cqbus.requests.handlers

import dev.botta.cqbus.identity.AnonymousIdentity
import dev.botta.cqbus.identity.Identity
import dev.botta.cqbus.requests.Request

fun interface RequestHandler<TRequest: Request<TResponse>, TResponse> {
    fun execute(request: TRequest): TResponse = execute(request, AnonymousIdentity())
    fun execute(request: TRequest, identity: Identity): TResponse
}
