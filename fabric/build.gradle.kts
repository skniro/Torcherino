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
            serverWithGui()
        }
    }

    accessWidener = file("src/main/resources/torcherino.accesswidener")
}

dependencies {
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings(loom.layered{
        this.officialMojangMappings()
        this.crane("dev.architectury:crane:1.16.5+build.16")
    })
}

repositories {
    maven {
        name = "Shedaniel"
        url = uri("https://maven.shedaniel.me/")
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "TerraformersMC"
                url = uri("https://maven.terraformersmc.com/")
            }
        }
        filter {
            includeGroup("com.terraformersmc")
        }
    }
}

val excludeFabric: (ExternalModuleDependency) -> Unit = {
    it.exclude("net.fabricmc")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${properties["fabric_loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_api_version"]}")

    modRuntime("io.github.prospector:modmenu:${properties["modmenu_version"]}")

    modRuntime("me.shedaniel:RoughlyEnoughItems:${properties["rei_version"]}")
}

tasks.withType<ProcessResources> {
    val props = mutableMapOf("version" to properties["mod_version"]) // Needs to be mutable
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}
