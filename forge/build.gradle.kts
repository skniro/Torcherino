plugins {
    id("dev.architectury.loom")
}

loom {
    silentMojangMappingsLicense()
    useFabricMixin = true
    runs {
        named("client") {
            vmArgs("-XX:+IgnoreUnrecognizedVMOptions")
            ideConfigGenerated(false)
        }
        named("server") {
            vmArgs("-XX:+IgnoreUnrecognizedVMOptions")
            ideConfigGenerated(false)
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings(loom.layered{
        this.officialMojangMappings()
        this.crane("dev.architectury:crane:1.16.5+build.16")
    })
}

repositories {
    exclusiveContent {
        forRepository {
            maven {
                // JEI maven
                name = "Progwml6 maven"
                url = uri("https://dvs1.progwml6.com/files/maven/")
            }
        }
        filter {
            includeGroup("mezz.jei")
        }
    }
}

dependencies {
    forge("net.minecraftforge:forge:${properties["minecraft_version"]}-${properties["forge_version"]}")

    modRuntime("mezz.jei:jei-${properties["minecraft_version"]}:${properties["jei_version"]}")
}

tasks.withType<ProcessResources>() {
    val props = mutableMapOf("version" to properties["mod_version"]) // Needs to be mutable
    inputs.properties(props)
    filesMatching("META-INF/mods.toml") {
        expand(props)
    }
}
