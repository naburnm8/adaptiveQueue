plugins {
    kotlin("jvm") version "2.2.10"
    `maven-publish`
}

group = "ru.bmstu.naburnm8"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "ru.bmstu.naburnm8"
            artifactId = "adaptiveQueue"
            version = "1.0-SNAPSHOT"

            from(components["kotlin"])
        }
    }
}