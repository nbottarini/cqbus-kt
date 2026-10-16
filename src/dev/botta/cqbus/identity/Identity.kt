package dev.botta.cqbus.identity

interface Identity {
    val name: String
    val isAuthenticated: Boolean
    val authenticationType: String?
    val grants: List<String>
    val properties: Map<String, Any>
}
