[![Maven](https://img.shields.io/maven-central/v/com.nbottarini/asimov-cqbus.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.nbottarini%22%20AND%20a%3A%22asimov-cqbus%22)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![CI Status](https://github.com/nbottarini/asimov-cqbus-kt/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/nbottarini/asimov-cqbus-kt/actions?query=branch%3Amain+workflow%3Aci)

# Asimov CQBus
Simple kotlin/java command &amp; query bus. For use in CQRS and Clean Architecture / Hexagonal projects.

## Installation

#### Gradle (Kotlin)

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.nbottarini:asimov-cqbus:0.5.4")
}
```

#### Gradle (Groovy)

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.nbottarini:asimov-cqbus:0.5.4'
}
```

#### Maven

```xml
<dependency>
    <groupId>com.nbottarini</groupId>
    <artifactId>asimov-cqbus</artifactId>
    <version>0.5.4</version>
</dependency>
```

## Usage

### Commands
```kotlin
class CreateFullName(val firstName: String, val lastName: String): Command<String>

class CreateFullNameHandler: RequestHandler<CreateFullName, String> {
    override fun execute(request: CreateFullName, identity: Identity): String {
        return request.firstName + " " + request.lastName
    }
}

val cqBus = CQBus()
cqBus.registerHandler { CreateFullNameHandler() }

cqBus.execute(CreateFullName("John", "Doe")) // returns "John Doe"
```

### Queries
```kotlin
class GetNews: Query<List<String>>

class GetNewsHandler: RequestHandler<GetNews, List<String>> {
    override fun execute(request: GetNews, identity: Identity): List<String> {
        return listOf("news 1", "news 2")
    }
}

val cqBus = CQBus()
cqBus.registerHandler { GetNewsHandler() }

cqBus.execute(GetNews()) // returns "news 1", "news 2"
```

### Execution Identity

```kotlin
class UserIdentity(override val name: String): Identity {
    override val isAuthenticated = true
    override val authenticationType: String? = null
    override val roles = listOf<String>()
    override val properties = mapOf<String, Any>()
}

class MyCommand: Command<String>

class MyCommandHandler: RequestHandler<MyCommand, String> {
    override fun execute(request: MyCommand, identity: Identity): String {
        return identity.name
    }
}

val cqBus = CQBus()
cqBus.registerHandler { MyCommandHandler() }

cqBus.execute(MyCommand(), ExecutionContext().withIdentity(UserIdentity("Alice"))) // returns "Alice"
cqBus.execute(MyCommand()) // returns "Anonymous"
```

### Context-aware handlers

```kotlin
class MyCommand: Command<String>

class MyCommandHandler: ContextAwareRequestHandler<MyCommand, String> {
    override fun execute(request: MyCommand, context: ExecutionContext): String {
        return context["some-key"] as String
    }
}

val cqBus = CQBus()
cqBus.registerHandler { MyCommandHandler() }

cqBus.execute(MyCommand(), ExecutionContext().with("some-key", "some-value")) // returns "some-value"
```

### Middlewares

```kotlin
class MyCommand: Command<String>

class MyCommandHandler: RequestHandler<MyCommand, String> {
    override fun execute(request: MyCommand, identity: Identity): String {
        return "handler"
    }
}

class LoggingMiddleware(private val log: MutableList<String>, private val suffix: String = ""): Middleware {
    override fun <T: Request<R>, R> execute(request: T, next: (T) -> R, context: ExecutionContext): R {
        log.add("before$suffix")
        val result = next(request)
        log.add("after$suffix")
        return result
    }
}

val log = mutableListOf<String>()
val cqBus = CQBus()
cqBus.registerHandler { MyCommandHandler() }
cqBus.registerMiddleware { LoggingMiddleware(log, "1") }
cqBus.registerMiddleware { LoggingMiddleware(log, "2") }

cqBus.execute(MyCommand())

// log contains "before2", "before1", "handler", "after1", "after2"
```
