package com.nbottarini.asimov.cqbus.identity

interface Identity {
    val name: String
    val isAuthenticated: Boolean
    val authenticationType: String?
    val roles: List<String>
    val properties: Map<String, Any>
}
