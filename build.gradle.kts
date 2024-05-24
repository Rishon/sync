plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("org.jetbrains.kotlin.jvm") version "2.0.0-RC2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.rishon.sync"
version = "1.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")

    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("redis.clients:jedis:5.1.2")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("me.lucko:jar-relocator:1.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.1")
}

val targetJavaVersion = 21
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.shadowJar {
    archiveBaseName.set("sync")
    archiveClassifier.set("")
    mergeServiceFiles()
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
