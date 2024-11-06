plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "me.dave"
version = "2.1.3-beta1"

repositories {
    mavenCentral() // bStats
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven("https://repo.codemc.io/repository/maven-releases/") // PacketEvents
    maven("https://maven.playpro.com") // CoreProtect
    maven("https://repo.lushplugins.org/releases") // LushLib
    maven("https://repo.lushplugins.org/snapshots") // LushLib
    maven("https://repo.william278.net/releases") // HuskClaims, HuskTowns
    maven("https://jitpack.io") // GriefPrevention, RealisticBiomes, Lands
}

dependencies {
    // Dependencies
    compileOnly("org.spigotmc:spigot-api:${findProperty("minecraftVersion")}-R0.1-SNAPSHOT")

    // Soft Dependencies
    compileOnly("com.comphenix.protocol:ProtocolLib:${findProperty("protocollibVersion")}")
    compileOnly("com.github.retrooper:packetevents-spigot:${findProperty("packetEventsVersion")}")
    compileOnly("net.coreprotect:coreprotect:${findProperty("coreprotectVersion")}")
    compileOnly("com.github.TechFortress:GriefPrevention:${findProperty("griefpreventionVersion")}")
    compileOnly("net.william278.huskclaims:huskclaims-bukkit:${findProperty("huskclaimsVersion")}")
    compileOnly("net.william278:husktowns:${findProperty("husktownsVersion")}")
    compileOnly("com.github.angeschossen:LandsAPI:${findProperty("landsVersion")}")
    compileOnly("com.github.Maroon28:RealisticBiomes:${findProperty("realisticbiomesVersion")}")

    // Libraries
    implementation("org.bstats:bstats-bukkit:${findProperty("bstatsVersion")}")
    implementation("org.lushplugins:LushLib:${findProperty("lushlibVersion")}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))

    withSourcesJar()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("org.bstats", "me.dave.gardeningtweaks.libraries.bstats")
        relocate("me.dave.chatcolorhandler", "me.dave.gardeningtweaks.libraries.chatcolor")

        minimize()

        val folder = System.getenv("pluginFolder")
        if (folder != null) {
            destinationDirectory.set(file(folder))
        }
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