import com.gitlab.ninjaphenix.gradle.api.task.MinifyJsonTask
import com.gitlab.ninjaphenix.gradle.api.task.ParamLocalObfuscatorTask
import org.gradle.jvm.tasks.Jar

plugins {
    java
    id("dev.architectury.loom").version("0.7.4-SNAPSHOT").apply(false)
    id("com.gitlab.ninjaphenix.gradle-utils").version("0.1.0-beta.4")
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

    val remapJarTask : net.fabricmc.loom.task.RemapJarTask = tasks.getByName<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}-fat.jar")
    }

    tasks.getByName<Jar>("jar") {
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}-dev.jar")
    }

    val minifyJarTask = tasks.register<MinifyJsonTask>("minJar") {
        input.set(remapJarTask.outputs.files.singleFile)
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}-min.jar")
        dependsOn(remapJarTask)
    }

    val releaseJarTask = tasks.register<ParamLocalObfuscatorTask>("releaseJar") {
        input.set(minifyJarTask.get().outputs.files.singleFile)
        archiveFileName.set("${properties["archivesBaseName"]}-${properties["mod_version"]}+${properties["minecraft_version"]}.jar")
        from(rootDir.resolve("LICENSE"))
        dependsOn(minifyJarTask)
    }

    tasks.getByName("build") {
        dependsOn(releaseJarTask)
    }
}

tasks.register("buildMod") {
    subprojects.forEach {
        dependsOn(it.tasks["build"])
    }
}
