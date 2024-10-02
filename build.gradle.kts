plugins {
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

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    repositories {
        maven {
            name = "craftsapiens"
            url = uri("http://node.craftsapiens.com.br:50021/releases")
            credentials(PasswordCredentials::class)
            isAllowInsecureProtocol = true
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "codes.shiftmc"
            artifactId = "streaming"
            version = "1.5.6"
            from(components["java"])
        }
    }
}