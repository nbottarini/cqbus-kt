package dev.botta.cqbus

class CannotAccessInternalRequestError(requestName: String):
    Exception("Cannot access internal request $requestName. Internal requests not enabled")
