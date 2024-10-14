package dev.celestialfault.compacting

import com.mojang.logging.LogUtils
import dev.celestialfault.compacting.config.Config
import dev.celestialfault.compacting.mixin.ChatHudAccessor
import dev.celestialfault.compacting.util.Timestamp
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.slf4j.Logger
import kotlin.time.Duration.Companion.seconds

object Compacting : ClientModInitializer {
	val LOG: Logger = LogUtils.getLogger()

	private val messages: MutableMap<Text, Message> = mutableMapOf()
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
	}

	private fun remove(message: Message) {
		var hud = MinecraftClient.getInstance().inGameHud.chatHud as ChatHudAccessor
		message.lastLine?.let(hud.messages::remove)
		message.lastVisible.forEach(hud.visibleMessages::remove)
		message.dividers.forEach(this::remove)
		message.dividers.clear()
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
	fun compact(text: Text): Message? {
		if(!Config.enabled) return null
		prune()

		var message: Message? = messages[text]
		if(message == null) {
			message = Message(text.copy())
			if(!message.isDivider) messages.put(text, message)
		}
		processDivider(message)

		message.timesSeen++
		message.lastSeen = Timestamp.now()

		if(message.shouldCompact) remove(message)
		return message
	}

	@JvmStatic
	fun clear() {
		messages.clear()
		currentDividerSet = null
	}
}
