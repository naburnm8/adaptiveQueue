plugins {
    kotlin("jvm") version "2.2.10"
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