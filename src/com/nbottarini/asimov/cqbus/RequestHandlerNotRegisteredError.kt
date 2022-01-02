package com.nbottarini.asimov.cqbus

class RequestHandlerNotRegisteredError(requestName: String): Exception("RequestHandler not registered for request $requestName")
