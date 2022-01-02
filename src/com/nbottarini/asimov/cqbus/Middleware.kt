package com.nbottarini.asimov.cqbus

import com.nbottarini.asimov.cqbus.requests.Request

interface Middleware {
    fun <T: Request<R>, R> execute(request: T, next: (T) -> R, context: ExecutionContext): R
}
