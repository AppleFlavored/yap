plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.5"
    application
}

group = "dev.flavored"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-core:1.5.16")
    implementation("ch.qos.logback:logback-classic:1.5.16")
}

kotlin {
    jvmToolchain(23)
}

application {
    mainClass.set("dev.flavored.yap.Application")
}