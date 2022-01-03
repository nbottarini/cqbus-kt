package com.nbottarini.asimov.cqbus

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExecutionContextTest {
    @Test
    fun `can get data`() {
        context["some-key"] = "some-value"

        assertThat(context["some-key"]).isEqualTo("some-value")
    }

    @Test
    fun `can get data by type name`() {
        context.set(User("alice"))

        assertThat(context.get<User>()?.name).isEqualTo("alice")
    }

    @Test
    fun `returns null if type not set`() {
        assertThat(context.get<User>()).isNull()
    }

    @Test
    fun has() {
        context["some-key"] = "some-value"

        assertThat(context.has("some-key")).isTrue
        assertThat(context.has("some-other-key")).isFalse
    }

    @Test
    fun `has with type name`() {
        context.set(User("alice"))

        assertThat(context.has<User>()).isTrue
    }

    private val context = ExecutionContext()

    private data class User(val name: String)
}
