package dev.botta.cqbus

import dev.botta.cqbus.requests.Request

interface Middleware {
    fun <T: Request<R>, R> execute(request: T, next: (T) -> R, context: ExecutionContext): R
}
