@file:Suppress("UnstableApiUsage")

plugins {
    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

val minecraft = stonecutter.current.version
val mcVersion = stonecutter.current.project.substringBeforeLast('-')
val accessWidenerFilepath = "src/main/resources/${property("mod.id")}.accesswidener"

val modVer = "${property("mod.version")}+${property("deps.sodium_compiled_against")}-mc${property("deps.minecraft")}-fabric"
version = modVer
base.archivesName = property("mod.id") as String

repositories {
    mavenLocal()
    maven {
        name = "Terraformers (Mod Menu)"
        url = uri("https://maven.terraformersmc.com/releases/")
        content {
            includeGroupAndSubgroups("com.terraformersmc")
        }
    }
    maven {
        name = "Caffeine MC (sodium)"
        url = uri("https://maven.caffeinemc.net/releases")
    }
    maven {
        name = "Caffeine MC Snapshots (sodium)"
        url = uri("https://maven.caffeinemc.net/snapshots")
    }
    maven {
        name = "Modrinth (iris)"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("deps.minecraft")}")
    implementation("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric-api")}")

    // Mod Menu
    if (hasProperty("deps.modmenu")) {
        api("com.terraformersmc:modmenu:${property("deps.modmenu")}")
    } else {
        compileOnly("com.terraformersmc:modmenu:18.0.0-alpha.3")
    }

    // Sodium
    implementation("net.caffeinemc:sodium-fabric:${property("deps.sodium")}")

    // iris
    compileOnly("maven.modrinth:iris:${property("deps.iris")}-fabric")
}

stonecutter {
}

loom {
    if(project.file(accessWidenerFilepath).exists()) {
        accessWidenerPath = project.file(accessWidenerFilepath)
    }

    runConfigs.all {
        runDir = "../../run"
        isIdeConfigGenerated = true
    }
}

configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:${property("deps.fabric-loader")}")
    }
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["version"] = modVer
        this["minecraft"] = prop("dep_str.minecraft")
        this["id"] = prop("mod.id")
        this["group"] = prop("mod.group")
        this["description"] = prop("mod.description")
        this["name"] = prop("mod.name")
        this["source_url"] = prop("mod.source_url")
        this["issue_tracker"] = prop("mod.issue_tracker")
        this["icon"] = prop("mod.icon")
        this["license"] = prop("mod.license")
        this["fabric_loader_dep_str"] = prop("dep_str.fabric-loader")
        this["fabric_api_dep_str"] = prop("dep_str.fabric-api")
        this["sodium_dep_str"] = prop("dep_str.sodium")
        this["java_ver"] = java.targetCompatibility.majorVersion
    }

    if(project.file(accessWidenerFilepath).exists()) {
        props["accesswidener_field"] = "\"accessWidener\": \"${prop("mod.id")}.accesswidener\","
    } else {
        props["accesswidener_field"] = ""
    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml", "*.mixins.json")) {
        expand(props)
    }
}

tasks.named("processResources") {
    dependsOn(":${stonecutter.current.project}:stonecutterGenerate")
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/mods.toml", "**/accesstransformer.cfg", "neoforge.mods.toml", "mods.toml", "accesstransformer.cfg")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

java {
    withSourcesJar()
    val javaCompat = JavaVersion.VERSION_25
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

publishMods {
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

    // one of BETA, ALPHA, STABLE
    type = STABLE
    displayName = "[Fabric] v${property("mod.version")} for mc ${stonecutter.current.version}"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("fabric")

    dryRun = boolProperty("publish.dry_run")

    if(hasProperty("publish.modrinth")) {
        modrinth {
            projectId = property("publish.modrinth") as String
            accessToken = env.MODRINTH_API_KEY.orNull()
            minecraftVersions.add(property("deps.minecraft").toString())
            minecraftVersions.addAll(additionalVersions)
            requires("fabric-api")
            requires("sodium")
            optional("modmenu")
        }
    }

    if(hasProperty("publish.curseforge")) {
        curseforge {
            projectId = property("publish.curseforge") as String
            accessToken = env.CURSEFORGE_API_KEY.orNull()
            minecraftVersions.add(stonecutter.current.version)
            minecraftVersions.addAll(additionalVersions)
            requires("fabric-api")
            requires("sodium")
        }
    }
}


fun bool(str: String) : Boolean {
    return str.lowercase().startsWith("t")
}

fun boolProperty(key: String) : Boolean {
    if(!hasProperty(key)){
        return false
    }
    return bool(property(key).toString())
}
