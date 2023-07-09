plugins {
    java
    `kotlin-dsl`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "me.dave"
version = "1.3"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/")}
    maven { url = uri("https://maven.playpro.com")}
    maven { url = uri("https://jitpack.io")}
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("net.coreprotect:coreprotect:21.3")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("com.github.Maroon28:RealisticBiomes:3d292ea32a")
    shadow("org.enchantedskies:EnchantedStorage:1.0.6-SNAPSHOT")
    shadow("com.github.CoolDCB:ChatColorHandler:v1.2.3")
}

java {
    configurations.shadow.get().dependencies.remove(dependencies.gradleApi())
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.shadowJar {
    minimize()
    configurations = listOf(project.configurations.shadow.get())
    var folder = System.getenv("pluginFolder_1-20")
    if (folder == null) folder = "/Users/davidbryce/IdeaProjects/PluginBuilds"
    archiveFileName.set("${project.name}-${project.version}.jar")
    destinationDirectory.set(file(folder))
}

// Handles version variables
tasks.processResources {
    expand(project.properties)

    inputs.property("version", rootProject.version)
    filesMatching("plugin.yml") {
        expand("version" to rootProject.version)
    }
}