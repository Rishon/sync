plugins {
    kotlin("jvm") version "2.2.21"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("com.gradleup.shadow") version "9.2.2"
    id("maven-publish")
}

group = "dev.rishon.sync"
version = "1.2"

val serverVersion = "1.21.9-rc1"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // Paper
    paperweight.paperDevBundle("$serverVersion-R0.1-SNAPSHOT")

    // Hooks
    compileOnly("me.clip:placeholderapi:2.11.6")

    // Data
    implementation("redis.clients:jedis:7.0.0")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("me.lucko:jar-relocator:1.7")
}

val targetJavaVersion = 21
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    archiveFileName = "${project.name}-${project.version}.jar"
    mergeServiceFiles()

    relocate("com.zaxxer.hikari", "dev.rishon.libs.hikari")
    relocate("redis.clients.jedis", "dev.rishon.libs.jedis")
    relocate("me.lucko.jarrelocator", "dev.rishon.libs.jarrelocator")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

publishing {
    repositories {
        maven {
            name = "seladevelopment-repo"
            url = uri("https://repo.rishon.systems/releases")
            credentials {
                username = System.getenv("MAVEN_NAME")
                password = System.getenv("MAVEN_SECRET")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "systems.rishon"
            artifactId = "sync"
            version = "${project.version}"
            from(components["java"])
        }
    }
}