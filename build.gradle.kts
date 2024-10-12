plugins {
	id("fabric-loom")
    kotlin("jvm") version("2.0.20")
	//id("dev.kikugie.j52j")
	id("me.modmuss50.mod-publish-plugin")
}

class ModData {
	val id = property("mod.id").toString()
	val version = property("mod.version").toString()
	val group = property("mod.group").toString()
}

class ModDependencies {
	operator fun get(name: String) = property("deps.$name").toString()
}

val mod = ModData()
val deps = ModDependencies()
val mcVersion = stonecutter.current.version
val mcDep = property("mod.mc_dep").toString()

version = "${mod.version}+$mcVersion"
group = mod.group
base { archivesName.set(mod.id) }

repositories {
	maven("https://maven.isxander.dev/releases")
	maven("https://maven.terraformersmc.com/")
}

dependencies {
	minecraft("com.mojang:minecraft:$mcVersion")
	mappings("net.fabricmc:yarn:$mcVersion+build.${deps["yarn_build"]}:v2")
	modImplementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${deps["fabric_language_kotlin"]}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${deps["fabric_api"]}")

	modImplementation("com.terraformersmc:modmenu:${deps["modmenu"]}")
	modImplementation("dev.isxander:yet-another-config-lib:${deps["yacl"]}-fabric")
}

loom {
	decompilers {
		get("vineflower").apply { // Adds names to lambdas - useful for mixins
			options.put("mark-corresponding-synthetics", "1")
		}
	}

	runConfigs.all {
		ideConfigGenerated(true)
		vmArgs("-Dmixin.debug.export=true")
		runDir = "../../run"
	}
}

java {
	val java = if (stonecutter.eval(mcVersion, ">=1.20.6")) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	targetCompatibility = java
	sourceCompatibility = java
}

tasks.processResources {
	inputs.property("version", mod.version)
	inputs.property("mcdep", mcDep)

	val map = mapOf(
		"version" to mod.version,
		"mcdep" to mcDep,
	)

	filesMatching("fabric.mod.json") { expand(map) }
}

tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(tasks.remapJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
	dependsOn("build")
}

publishMods {
	file = tasks.remapJar.get().archiveFile
	additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
	displayName = "${mod.version} for ${property("mod.mc_title")}"
	version = "${mod.version}+$mcVersion"
	changelog = rootProject.file("CHANGELOG.md").readText()
	type = STABLE
	modLoaders.add("fabric")

	dryRun = true //providers.environmentVariable("MODRINTH_TOKEN").getOrNull()

	modrinth {
		projectId = property("publish.modrinth").toString()
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		minecraftVersions.addAll(property("mod.mc_targets").toString().split(" "))
		requires("fabric-language-kotlin")
		optional("fabric-api")
		optional("yacl")
		optional("modmenu")
	}
}
