plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
}

tasks.test {
    useJUnitPlatform()
}