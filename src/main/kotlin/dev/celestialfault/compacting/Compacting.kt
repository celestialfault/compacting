package dev.celestialfault.compacting

import com.mojang.logging.LogUtils
import dev.celestialfault.compacting.config.Config
import dev.celestialfault.compacting.mixin.ChatHudAccessor
import dev.celestialfault.compacting.util.Timestamp
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.hud.ChatHudLine
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.slf4j.Logger
import kotlin.time.Duration.Companion.seconds

object Compacting : ClientModInitializer {
	val LOG: Logger = LogUtils.getLogger()
	private val messages: MutableMap<Text, Message> = mutableMapOf()
	private val dividers: MutableList<Message> = mutableListOf()
	private var currentDividerSet: MutableList<Message>? = null

	override fun onInitializeClient() {
		Config.load()
		if(FabricLoader.getInstance().isModLoaded("fabric-message-api-v1")) {
			removeBlank()
		}
	}

	private fun removeBlank() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
			if(!Config.removeBlank) true else !Formatting.strip(message.string)!!.isBlank()
		}
	}

	private fun prune() {
		messages.values.removeIf(Message::isOld)
		dividers.removeIf(Message::isOld)
	}

	private fun remove(message: Message) {
		var hud = MinecraftClient.getInstance().inGameHud.chatHud as ChatHudAccessor
		hud.messages.remove(message.lastLine)
		message.lastVisible.forEach(hud.visibleMessages::remove)
		message.dividers.forEach(this::remove)
		message.dividers.clear()
		dividers.remove(message)
	}

	private fun find(line: ChatHudLine): Message? {
		// note that we can't actually use a hashmap lookup here as we modify the text to add the compact count,
		// changing the hash and therefore breaking such a lookup.
		return messages.values.firstOrNull { it.lastLine === line } ?: dividers.firstOrNull { it.lastLine === line }
	}

	private fun associateDividers(lastDivider: Message) {
		val divided = currentDividerSet
		check(divided != null)
		if(divided.size < 2) return
		divided[1].dividers.add(divided[0])
		divided.last().dividers.add(lastDivider)
	}

	private fun processDivider(message: Message) {
		currentDividerSet?.let {
			if(it.first().lastSeen.elapsedSince() > 5.seconds) {
				LOG.warn("Second divider wasn't received after 5 seconds!")
				currentDividerSet = null
			}
		}
		if(message.isDivider) {
			dividers.add(message)
			if(currentDividerSet == null) {
				currentDividerSet = mutableListOf()
			} else {
				associateDividers(message)
				currentDividerSet = null
			}
		}
		currentDividerSet?.add(message)
	}

	@JvmStatic
	fun compact(line: ChatHudLine): ChatHudLine {
		if(!Config.enabled) return line
		prune()

		val message = messages[line.content] ?: Message(line).also { if(!it.isDivider) messages.put(line.content, it) }
		processDivider(message)

		message.timesSeen++
		message.lastSeen = Timestamp.now()
		if(!message.shouldCompact) {
			message.lastLine = line
			return line
		}

		remove(message)
		val newLine = ChatHudLine(line.creationTick, message.textWithCounter, line.signature, line.indicator)
		message.lastLine = newLine
		message.lastVisible.clear()
		return newLine
	}

	@JvmStatic
	fun associate(line: ChatHudLine, visible: ChatHudLine.Visible) {
		find(line)?.lastVisible?.add(visible)
	}

	@JvmStatic
	fun clear() {
		messages.clear()
		dividers.clear()
		currentDividerSet = null
	}
}
