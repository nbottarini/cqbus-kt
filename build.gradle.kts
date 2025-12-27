import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("jvm") version "2.1.20"
    id("com.vanniktech.maven.publish") version "0.32.0"
    id("dev.botta.kotlin-conventions") version "0.4.2"
}

group = "dev.botta"
version = "1.2.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(23)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.1")
    testImplementation("org.assertj:assertj-core:3.27.6")
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
