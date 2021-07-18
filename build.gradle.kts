import com.gitlab.ninjaphenix.gradle.task.MinifyJsonTask
import org.gradle.jvm.tasks.Jar

plugins {
    java
    id("dev.architectury.loom").version("0.9.0.147").apply(false)
    id("com.gitlab.ninjaphenix.gradle-utils").version("0.0.20")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "dev.architectury.loom")

    group = properties["maven_group"] as String
    version = properties["mod_version"] as String
    base.archivesName.set(properties["archives_base_name"] as String)
    buildDir = rootDir.resolve("build/${project.name}")

    java {
        sourceCompatibility = JavaVersion.toVersion(properties["mod_java_version"] as String)
        targetCompatibility = JavaVersion.toVersion(properties["mod_java_version"] as String)
    }

    sourceSets {
        main {
            java {
                setSrcDirs(listOf(
                        "src/main/java",
                        rootDir.resolve("common/${project.name}Src/main/java"),
                ))
            }
            resources {
                setSrcDirs(listOf(
                        "src/main/resources",
                        rootDir.resolve("common/${project.name}Src/main/resources")
                ))
            }
        }
    }

    repositories {
        exclusiveContent {
            forRepository {
                maven {
                    name = "Architectury Maven ( Crane )"
                    url = uri("https://maven.architectury.dev/")
                }
            }
            filter {
                includeModule("dev.architectury", "crane")
            }
        }
    }

    val minecraft_java_version : String by project
    val isNotIdeaSync = System.getProperties().containsKey("idea.sync.active").not()

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (isNotIdeaSync) {
            options.release.set(minecraft_java_version.toInt())
        }
    }

    tasks.withType<JavaExec>().configureEach {
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(minecraft_java_version.toInt()))
        })
    }

    val remapJarTask : Jar = tasks.getByName<Jar>("remapJar") {
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}-fat.jar")
        from("LICENSE")
    }

    tasks.getByName<Jar>("jar") {
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}-dev.jar")
    }

    val minifyJarTask = tasks.register<MinifyJsonTask>("minJar") {
        parent.set(remapJarTask.outputs.files.singleFile)
        filePatterns.set(listOf("**/*.json", "**/*.mcmeta"))
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}.jar")
        dependsOn(remapJarTask)
    }

    tasks.getByName("build") {
        dependsOn(minifyJarTask)
    }
}

tasks.register("buildMod") {
    subprojects.forEach {
        dependsOn(it.tasks["build"])
    }
}
