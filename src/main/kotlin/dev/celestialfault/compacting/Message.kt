package dev.celestialfault.compacting

import dev.celestialfault.compacting.config.Config
import dev.celestialfault.compacting.util.Timestamp
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.time.Duration.Companion.seconds

class Message(val text: MutableText) {
	var lastLine: ChatHudLine? = null
	val lastVisible: MutableList<ChatHudLine.Visible> = mutableListOf()
	val dividers: MutableList<Message> = mutableListOf()

	var timesSeen: Int = 0
	var lastSeen: Timestamp = Timestamp.now()

	val isDivider: Boolean by lazy {
		val message = Formatting.strip(text.string)!!
		message.length > 5 && message.all { it == '-' || it == '=' || it == '\u25AC' }
	}

	val textWithCounter: Text get() = if(timesSeen == 1) text else text
		.copy()
		.append(Text.literal(" ($timesSeen)").setStyle(Style.EMPTY.withExclusiveFormatting(Formatting.GRAY)))

	@get:JvmName("shouldCompact")
	val shouldCompact: Boolean
		get() = timesSeen > 1 && !isDivider

	fun isOld(): Boolean = lastSeen.elapsedSince() >= Config.timeout.seconds
}
