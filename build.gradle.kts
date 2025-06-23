plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version("8.3.0")
    id("xyz.jpenilla.run-paper") version("2.2.4")
}

group = "org.lushplugins"
version = "3.0.7"

repositories {
    mavenLocal()
    mavenCentral() // bStats, Lamp
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://repo.codemc.io/repository/maven-releases/") // PacketEvents
    maven("https://maven.playpro.com") // CoreProtect
    maven("https://repo.lushplugins.org/snapshots") // LushLib
    maven("https://repo.william278.net/releases") // HuskClaims, HuskTowns
    maven("https://jitpack.io") // GriefPrevention, RealisticBiomes, Lands
}

dependencies {
    // Dependencies
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")

    // Soft Dependencies
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("com.github.retrooper:packetevents-spigot:2.8.0")
    compileOnly("com.github.TechFortress:GriefPrevention:17.0.0")
    compileOnly("net.william278.huskclaims:huskclaims-bukkit:1.5.9")
    compileOnly("net.william278.husktowns:husktowns-bukkit:3.1.2")
    compileOnly("com.github.angeschossen:LandsAPI:7.15.20")
    compileOnly("com.github.Maroon28:RealisticBiomes:3d292ea32a")

    // Libraries
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("org.lushplugins:LushLib:0.10.75")
    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12")
    implementation("org.lushplugins.pluginupdater:PluginUpdater-API:1.0.3")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))

    registerFeature("optional") {
        usingSourceSet(sourceSets["main"])
    }

    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("org.bstats", "org.lushplugins.gardeningtweaks.libraries.bstats")
        relocate("org.lushplugins.lushlib", "org.lushplugins.gardeningtweaks.libraries.lushlib")
        relocate("revxrsal.commands", "org.lushplugins.gardeningtweaks.libraries.lamp")
        relocate("org.lushplugins.pluginupdater", "org.lushplugins.gardeningtweaks.libraries.plugin")

        minimize()

        archiveFileName.set("${project.name}-${project.version}.jar")
    }

    processResources{
        filesMatching("plugin.yml") {
            expand(project.properties)
        }

        inputs.property("version", rootProject.version)
        filesMatching("plugin.yml") {
            expand("version" to rootProject.version)
        }
    }

    runServer {
        minecraftVersion("1.21.6")
    }
}

publishing {
    publishing {
        repositories {
            maven {
                name = "lushReleases"
                url = uri("https://repo.lushplugins.org/releases")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }

            maven {
                name = "lushSnapshots"
                url = uri("https://repo.lushplugins.org/snapshots")
                credentials(PasswordCredentials::class)
                authentication {
                    isAllowInsecureProtocol = true
                    create<BasicAuthentication>("basic")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = rootProject.name
                version = rootProject.version.toString()
                from(project.components["java"])
            }
        }
    }
}