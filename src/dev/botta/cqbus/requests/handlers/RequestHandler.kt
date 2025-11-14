package dev.botta.cqbus.requests.handlers

import dev.botta.cqbus.identity.*
import dev.botta.cqbus.requests.Request

fun interface RequestHandler<TRequest: Request<TResponse>, TResponse> {
    suspend fun execute(request: TRequest): TResponse = execute(request, AnonymousIdentity())
    suspend fun execute(request: TRequest, identity: Identity): TResponse
}
