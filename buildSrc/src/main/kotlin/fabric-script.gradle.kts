plugins {
    id("fabric-loom")
    id("io.github.dexman545.outlet")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

val transitiveInclude: Configuration by configurations.creating {
    exclude(group = "com.mojang")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "org.jetbrains.kotlinx")
}

dependencies {
    val gameVersion: String by properties
    outlet.mcVersionRange = properties["fabricDependencyVersions"] as String

    //
    // Fabric configuration
    //
    minecraft("com.mojang", "minecraft", gameVersion)
    mappings(loom.officialMojangMappings())
//    println("FabricLoader: " + outlet.loaderVersion() + ", " + outlet.fapiVersion())
//    modImplementation("net.fabricmc", "fabric-loader", outlet.loaderVersion())
//    modImplementation("net.fabricmc.fabric-api", "fabric-api", outlet.fapiVersion())
    modImplementation("net.fabricmc", "fabric-loader", "0.16.12")
    modImplementation("net.fabricmc.fabric-api", "fabric-api", "0.119.9+1.21.5")

    //
    // Kotlin libraries
    //
    val flkVersion = outlet.latestModrinthModVersion("fabric-language-kotlin", outlet.mcVersions())
    modImplementation("net.fabricmc", "fabric-language-kotlin", flkVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.+")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.+")

    //
    // Silk configuration (optional)
    //
    val silkVersion = properties["silkVersion"] as String
    println("Silk: $silkVersion")
    modImplementation(project.files("../libs/silk-core-1.11.1.jar"))
    modImplementation(project.files("../libs/silk-commands-1.11.1.jar"))
    modImplementation(project.files("../libs/silk-nbt-1.11.1.jar"))
    modImplementation(project.files("../libs/silk-network-1.11.1.jar"))

    //
    // AutoPickup library
    //
    modImplementation(project.files("../libs/auto-pickup-1.1.1.jar"))

    //
    // Permissions configuration (optional)
    //
    val usePermissions = properties["usePermissions"] as String == "true"
    if (usePermissions) {
        modImplementation(include("me.lucko", "fabric-permissions-api", "0.3.3"))
    }

    //
    // Ingame configuration (optional)
    //
    val useConfig = properties["useConfig"] as String == "true"
    if (useConfig) {
        modApi("com.terraformersmc", "modmenu", "9.+")
        modApi("me.shedaniel.cloth", "cloth-config-fabric", "13.+") {
            exclude("net.fabricmc.fabric-api")
        }
        transitiveInclude(implementation("org.yaml", "snakeyaml", "2.2"))
    }


    // Add all non-mod dependencies to the jar
    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
    }
}

tasks.processResources {
    println("-----" + outlet.mcVersionRange + " - ${properties["version"]}")
    filesMatching("fabric.mod.json") {
        val modrinthSlug = properties["modrinthProjectId"] as? String ?: properties["modid"] as String
        expand(
            mapOf(
                "modid" to properties["modid"] as String,
                "version" to properties["version"] as String,
                "name" to properties["projectName"] as String,
                "description" to properties["description"],
                "author" to properties["author"] as String,
                "license" to properties["licence"] as String,
                "modrinth" to modrinthSlug,
                "environment" to properties["environment"] as String,
                "mcversion" to outlet.mcVersionRange,
            )
        )
    }
}
