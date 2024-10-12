plugins {
	id("dev.kikugie.stonecutter")
	id("fabric-loom") version "1.7-SNAPSHOT" apply false
	//id("dev.kikugie.j52j") version "1.0.2" apply false // Enables asset processing by writing json5 files
	id("me.modmuss50.mod-publish-plugin") version "0.7.+" apply false
}
stonecutter active "1.21.1" /* [SC] DO NOT EDIT */

// Builds every version into `build/libs/{mod.version}/`
stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
	group = "project"
	ofTask("buildAndCollect")
}

/*
// Publishes every version
stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
	group = "project"
	ofTask("publishMods")
}
*/

stonecutter configureEach {
	/* https://stonecutter.kikugie.dev/ */
	swap("mod_version", "\"${property("mod.version")}\";")
	dependency("fapi", project.property("deps.fabric_api").toString())
}
