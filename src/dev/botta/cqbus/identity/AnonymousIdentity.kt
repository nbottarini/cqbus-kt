package dev.botta.cqbus.identity

class AnonymousIdentity: Identity {
    override val name = "Anonymous"
    override val isAuthenticated = false
    override val authenticationType: String? = null
    override val roles = listOf<String>()
    override val properties = mapOf<String, Any>()
}
