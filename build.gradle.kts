plugins {
    id("io.github.goooler.shadow") version "8.1.7"
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:7b180172ce")
    implementation("ch.qos.logback:logback-classic:1.5.6")

    implementation("org.bytedeco:javacv-platform:1.5.10")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "codes.shiftmc.streaming.Server"
    }
}