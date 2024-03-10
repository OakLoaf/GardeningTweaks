plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "me.dave"
version = "2.0.1"

repositories {
    mavenCentral() // bStats
    mavenLocal() // PlatyUtils
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") } // Spigot
    maven { url = uri("https://repo.dmulloy2.net/repository/public/")} // ProtocolLib
    maven { url = uri("https://maven.playpro.com")} // CoreProtect
    maven { url = uri("https://repo.william278.net/releases") } // HuskClaims, HuskTowns
    maven { url = uri("https://jitpack.io")} // ChatColorHandler, GardeningTweaks, GriefPrevention, RealisticBiomes, Lands
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.7.0")
    compileOnly("net.coreprotect:coreprotect:21.3")
    compileOnly("com.github.Maroon28:RealisticBiomes:3d292ea32a")
    compileOnly("net.william278.huskclaims:huskclaims-bukkit:1.0.1")
    compileOnly("net.william278:husktowns:2.6.1")
    compileOnly("com.github.TechFortress:GriefPrevention:16.18")
    compileOnly("com.github.angeschossen:LandsAPI:6.42.15")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation(files("libs/PlatyUtils-0.1.21.jar"))
    implementation("com.github.CoolDCB:ChatColorHandler:v2.5.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("org.bstats", "me.dave.gardeningtweaks.libraries.bstats")
        relocate("me.dave.chatcolorhandler", "me.dave.gardeningtweaks.libraries.chatcolor")

        val folder = System.getenv("pluginFolder_1-20")
        if (folder != null) destinationDirectory.set(file(folder))
        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources{
        expand(project.properties)

        inputs.property("version", rootProject.version)
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = group.toString()
            artifactId = rootProject.name
            version = version

            from(components["java"])
        }
    }
}