package com.nbottarini.cqbus

import com.nbottarini.cqbus.requests.Request

interface Middleware {
    fun <T: Request<R>, R> execute(request: T, next: (T) -> R, context: ExecutionContext): R
}
