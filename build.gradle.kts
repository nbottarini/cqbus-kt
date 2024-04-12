import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.vanniktech.maven.publish") version "0.28.0"
    id("dev.botta.kotlin-conventions") version "0.1.0"
}

group = "dev.botta"
version = "1.0.1"

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()
    coordinates(project.group.toString(), "cqbus", project.version.toString())

    pom {
        name.set("CQBus")
        description.set("Simple Kotlin and Java command/query bus")
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
            connection.set("scm:git:git://github.com/nbottarini/cqbus-kt.git")
            developerConnection.set("scm:git:ssh://github.com/nbottarini/cqbus-kt.git")
            url.set("https://github.com/nbottarini/cqbus-kt")
        }
    }
}
