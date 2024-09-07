import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

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
    implementation("com.corundumstudio.socketio:netty-socketio:2.0.11")
    implementation("uk.co.caprica:vlcj:4.8.3")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        minimize()
        manifest {
            attributes["Main-Class"] = "codes.shiftmc.streaming.Server"
        }
    }
}