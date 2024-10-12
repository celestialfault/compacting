package dev.celestialfault.compacting.config

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.stream.JsonWriter
import dev.celestialfault.compacting.Compacting
import net.fabricmc.loader.api.FabricLoader
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import kotlin.io.path.exists

object Config {
	private val ADAPTER = Gson().getAdapter(JsonObject::class.java)
	private val PATH = FabricLoader.getInstance().configDir.resolve("compacting.json")

	var enabled: Boolean = true
	var timeout: Int = 60
	var removeBlank: Boolean = false

	// we're using elvis operator to make the intended defaults clear
	@Suppress("NullableBooleanElvis")
	fun load() {
		if(!PATH.exists()) {
			save()
			return
		}

		try {
			val data = FileReader(PATH.toFile()).use { ADAPTER.fromJson(it) }
			this.enabled = data["enabled"]?.asBoolean ?: true
			this.timeout = data["timeout"]?.asInt ?: 60
			this.removeBlank = data["removeBlank"]?.asBoolean ?: false
		} catch(e: Exception) {
			Compacting.LOG.error("Failed to load config", e)
		}
	}

	fun save() {
		val obj = JsonObject()
		obj.addProperty("enabled", enabled)
		obj.addProperty("timeout", timeout)
		obj.addProperty("removeBlank", removeBlank)

		try {
			FileWriter(PATH.toFile()).use { writer -> JsonWriter(writer).use {
				it.setIndent("\t")
				ADAPTER.write(it, obj)
			}}
		} catch(e: IOException) {
			Compacting.LOG.error("Failed to save config", e)
		}
	}
}
