package dev.botta.cqbus

import dev.botta.cqbus.requests.Request

interface Middleware {
    suspend fun <T: Request<R>, R> execute(request: T, next: suspend (T) -> R, context: ExecutionContext): R
}
