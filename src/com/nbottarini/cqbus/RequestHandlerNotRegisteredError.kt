package com.nbottarini.cqbus

class RequestHandlerNotRegisteredError(requestName: String): Exception("RequestHandler not registered for request $requestName")
