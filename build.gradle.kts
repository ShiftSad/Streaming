plugins {
    id("io.github.goooler.shadow") version "8.1.7"
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:7b180172ce")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "codes.shiftmc.streaming.Server"
    }
}