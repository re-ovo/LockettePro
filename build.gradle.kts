plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    kotlin("jvm") version "1.6.0"
}

group = "me.rerere"
version = "3.0.0"

tasks {
    val targetJavaVersion = 8
    java {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    withType<JavaCompile>(){
        options.encoding = "UTF-8"
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from("src/main/resources") {
            include("**/*.yml")
            filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf(
                "version" to project.version
            ))
        }
        filesMatching("application.properties") {
            expand(project.properties)
        }
    }
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }

    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    shadowJar {
        dependencies {
            exclude(dependency("org.slf4j:.*"))
        }
        minimize()
        relocate("kotlin", "me.crafter.mc.lockettepro.thirdparty.kotlin")
        relocate("org.jetbrains", "me.crafter.mc.lockettepro.thirdparty.org.jetbrains")
        relocate("org.koin", "me.crafter.mc.lockettepro.thirdparty.org.koin")
        relocate("org.intellij", "me.crafter.mc.lockettepro.thirdparty.org.intellij")
    }
    build {
        dependsOn(shadowJar)
    }
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://jitpack.io")
}

dependencies {
    // kt std
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")

    // spigot api
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")

    // ProtocolLib
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")

    // Vault
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // local
    compileOnly(fileTree("libs"))
}