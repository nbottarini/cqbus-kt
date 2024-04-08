import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "dev.botta"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.2")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDirs("src")
        resources.srcDirs("resources")
    }
    sourceSets["test"].apply {
        kotlin.srcDir("test")
        resources.srcDir("test_resources")
    }
}

java {
//    withJavadocJar()
//    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    sourceSets["main"].apply {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
    sourceSets["test"].apply {
        java.srcDir("test")
        resources.srcDir("test_resources")
    }
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<Test>().configureEach {
    reports.html.required.set(false)
    reports.junitXml.required.set(false)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates("dev.botta", "cqbus", "1.0.0")

    pom {
        name.set("CQBus")
        description.set("Simple kotlin/java command and query bus. For use in CQRS and Clean Architecture / Hexagonal projects.")
        inceptionYear.set("2021")
        url.set("https://github.com/nbottarini/cqbus-kt")

        licenses {
            license {
                name.set("MIT License")
                url.set("http://www.opensource.org/licenses/mit-license.php")
                distribution.set("http://www.opensource.org/licenses/mit-license.php")
            }
        }

        developers {
            developer {
                id.set("nbottarini")
                name.set("Nicolas Bottarini")
                url.set("https://github.com/nbottarini/")
                email.set("nicolasbottarini@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/cqbus-kt.git")
            developerConnection.set("scm:git:ssh://github.com/cqbus-kt.git")
            url.set("https://github.com/nbottarini/cqbus-kt")
        }
    }
}
