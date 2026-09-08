plugins {
    `java-library`
    `maven-publish`
    id("java")
    id("com.gradleup.shadow") version "9.6.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.22"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("io.freefair.lombok") version "9.5.0"
}

repositories {
    mavenCentral()
    google()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.unnamed.team/repository/unnamed-public/")
}

dependencies {
    // Paper 1.21.11 dev bundle (latest as of 2026)
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    api("commons-io:commons-io:2.18.0")
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    // Resource pack API (Unnamed Team Creative)
    implementation("team.unnamed:creative-api:1.7.3")
    // Serializer for Minecraft format (ZIP / Folder)
    implementation("team.unnamed:creative-serializer-minecraft:1.7.3")
    // Javalin 7.x backend with JSON to object mapping support (requires Java 17+)
    implementation("io.javalin:javalin:7.2.3")
    implementation("org.slf4j:slf4j-simple:2.0.13")
    //implementation("com.fasterxml.jackson.core:jackson-databind:2.17.+")

    // Testing - JUnit 5 + MockBukkit for Paper 1.21
    // NOTE: Tests are currently disabled due to MockBukkit 1.21 registry issue (#1032)
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.107.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.0")
}

group = "io.github"
version = "1.0-SNAPSHOT"
description = "Origami"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

tasks {
    assemble {
        dependsOn(reobfJar)
        dependsOn(shadowJar)
    }
    runServer {
        dependsOn(assemble)
        minecraftVersion("1.21.11")
        // Enable debugging on port 5005
        jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005")
        // Better memory settings for development
        jvmArgs("-Xms1G", "-Xmx2G")
        // Aikar's flags for better performance
        jvmArgs(
            "-XX:+UseG1GC",
            "-XX:+ParallelRefProcEnabled",
            "-XX:MaxGCPauseMillis=200",
            "-XX:+UnlockExperimentalVMOptions",
            "-XX:+DisableExplicitGC",
            "-XX:+AlwaysPreTouch",
            "-XX:G1NewSizePercent=30",
            "-XX:G1MaxNewSizePercent=40",
            "-XX:G1HeapRegionSize=8M",
            "-XX:G1ReservePercent=20",
            "-XX:G1HeapWastePercent=5",
            "-XX:G1MixedGCCountTarget=4",
            "-XX:InitiatingHeapOccupancyPercent=15",
            "-XX:G1MixedGCLiveThresholdPercent=90",
            "-XX:G1RSetUpdatingPauseTimePercent=5",
            "-XX:SurvivorRatio=32",
            "-XX:+PerfDisableSharedMem",
            "-XX:MaxTenuringThreshold=1"
        )
    }
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
            showStandardStreams = true
        }
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
                "version" to project.version
        )
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}