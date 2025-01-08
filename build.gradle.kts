plugins {
    id("com.gradleup.shadow") version "8.3.5" // https://github.com/GradleUp/shadow
    `java-library`
    `maven-publish`
}

group = "com.andrew121410.mc"
version = "1.0"
description = "World1-6Jails"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

tasks {
    build {
        dependsOn("shadowJar")
    }

    jar {
        enabled = false
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveBaseName.set("World1-6Jails")
        archiveClassifier.set("")
        archiveVersion.set("")

//        relocate("org.bstats", "com.andrew121410.mc.world16jails.bstats")
    }
}

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
//    api("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.World1-6.World1-6Utils:World1-6Utils-Plugin:2f0bf39de7")
}