import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("io.github.goooler.shadow") version "8.1.7"
    id("java")
    `maven-publish`
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
        manifest {
            attributes["Main-Class"] = "codes.shiftmc.streaming.Server"
        }
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            // Set your groupId, artifactId, and version here
            groupId = "codes.shiftmc"
            artifactId = "streaming"
            version = "1.0.0"
        }
    }
    repositories {
        maven {
            name = "reposilite"
            url = uri("http://node.craftsapiens.com.br:50021/repository/maven-releases/")

            credentials {
                username = System.getenv("REPOSILITE_USERNAME")
                password = System.getenv("REPOSILITE_TOKEN")
            }
        }
    }
}