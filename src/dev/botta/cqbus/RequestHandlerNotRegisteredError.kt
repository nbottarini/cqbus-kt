package dev.botta.cqbus

class RequestHandlerNotRegisteredError(requestName: String): Exception("RequestHandler not registered for request $requestName")
