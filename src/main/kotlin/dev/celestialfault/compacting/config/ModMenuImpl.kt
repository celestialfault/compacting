package dev.celestialfault.compacting.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

object ModMenuImpl : ModMenuApi {
	override fun getModConfigScreenFactory() = ConfigScreenFactory { ConfigGui.generateScreen(it) }
}
