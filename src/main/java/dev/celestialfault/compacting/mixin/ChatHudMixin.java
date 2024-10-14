package dev.celestialfault.compacting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.celestialfault.compacting.Compacting;
import dev.celestialfault.compacting.Message;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatHud.class, priority = 9999)
abstract class ChatHudMixin {
	private final @Unique ThreadLocal<@Nullable Message> CURRENT = new ThreadLocal<>();

	@ModifyVariable(
		//? if >=1.20.5 {
		method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
		//?} else if >=1.19.4 {
		/*method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
		*///?}
		at = @At("HEAD"),
		argsOnly = true
	)
	public Text compacting$compactText(Text text) {
		var message = Compacting.compact(text);
		if(message == null) {
			CURRENT.remove();
			return text;
		}
		CURRENT.set(message);
		if(message.shouldCompact()) {
			return message.getTextWithCounter();
		}
		return text;
	}

	@WrapOperation(
		//? if >=1.20.5 {
		method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
		//?} else if >=1.19.4 {
		/*method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
		*///?}
		at = @At(
			value = "NEW",
			target = "(ILnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)Lnet/minecraft/client/gui/hud/ChatHudLine;"
		)
	)
	public ChatHudLine compacting$associateHudLine(int creationTick, Text text, MessageSignatureData messageSignatureData, MessageIndicator messageIndicator, Operation<ChatHudLine> original) {
		var line = original.call(creationTick, text, messageSignatureData, messageIndicator);
		var message = CURRENT.get();
		if(message != null) {
			message.setLastLine(line);
		}
		return line;
	}

	@WrapOperation(
		//? if >=1.20.5 {
		method = "addVisibleMessage",
		//?} else if >=1.19.4 {
		/*method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V",
		*///?}
		at = @At(
			value = "NEW",
			target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"
		)
	)
	public ChatHudLine.Visible compacting$associateVisibleLine(int tick, OrderedText text, MessageIndicator indicator,
	                                                           boolean endOfEntry, Operation<ChatHudLine.Visible> original) {
		var visible = original.call(tick, text, indicator, endOfEntry);
		var message = CURRENT.get();
		if(message != null) {
			message.getLastVisible().add(visible);
		}
		return visible;
	}

	@Inject(method = "clear", at = @At("TAIL"))
	public void compacting$clearMessages(boolean clearHistory, CallbackInfo ci) {
		Compacting.clear();
		CURRENT.remove();
	}
}
