package com.nbottarini.cqbus

import com.nbottarini.cqbus.identity.AnonymousIdentity
import com.nbottarini.cqbus.identity.Identity

class ExecutionContext {
    var identity: Identity = AnonymousIdentity()
    private val data: MutableMap<String, Any> = mutableMapOf()

    operator fun get(key: String) = data[key]

    fun <T: Any> get(type: Class<T>) = get(type.name) as T

    inline fun <reified T: Any> get() = get(T::class.java)

    operator fun set(key: String, value: Any) { data[key] = value }

    fun set(value: Any) = set(value.javaClass.name, value)

    fun with(key: String, value: Any) = apply { this[key] = value }

    fun with(value: Any) = with(value.javaClass.name, value)

    fun withIdentity(identity: Identity) = apply { this.identity = identity }

    fun has(key: String) = data.containsKey(key)

    fun has(type: Class<*>) = has(type.name)

    inline fun <reified T: Any> has() = has(T::class.java)
}
