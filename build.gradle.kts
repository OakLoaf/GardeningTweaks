plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

group = "me.dave"
version = "2.1.2"

repositories {
    mavenCentral() // bStats
    mavenLocal()
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot
    maven(url = "https://repo.dmulloy2.net/repository/public/") // ProtocolLib
    maven(url = "https://maven.playpro.com") // CoreProtect
    maven(url = "https://repo.lushplugins.org/releases") // LushLib
    maven(url = "https://repo.lushplugins.org/snapshots") // LushLib
    maven(url = "https://repo.william278.net/releases") // HuskClaims, HuskTowns
    maven(url = "https://jitpack.io") // GriefPrevention, RealisticBiomes, Lands
}

dependencies {
    // Dependencies
    compileOnly("org.spigotmc:spigot-api:${findProperty("minecraftVersion")}-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:${findProperty("protocollibVersion")}")

    // Soft Dependencies
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