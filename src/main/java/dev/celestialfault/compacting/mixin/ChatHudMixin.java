package dev.celestialfault.compacting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.celestialfault.compacting.Compacting;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatHud.class, priority = 9999)
abstract class ChatHudMixin {
	@WrapOperation(
		method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
		at = @At(
			value = "NEW",
			target = "(ILnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)Lnet/minecraft/client/gui/hud/ChatHudLine;"
		)
	)
	public ChatHudLine celestetweaks$compactChat(int ticks, Text message, MessageSignatureData sig, MessageIndicator indicator, Operation<ChatHudLine> original) {
		return Compacting.compact(original.call(ticks, message, sig, indicator));
	}

	@WrapOperation(
		method = "addVisibleMessage",
		at = @At(
			value = "NEW",
			target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"
		)
	)
	public ChatHudLine.Visible celestetweaks$associateVisibleLine(int tick, OrderedText text, MessageIndicator indicator,
	                                                              boolean endOfEntry, Operation<ChatHudLine.Visible> original,
	                                                              @Local(argsOnly = true) ChatHudLine line) {
		var visible = original.call(tick, text, indicator, endOfEntry);
		Compacting.associate(line, visible);
		return visible;
	}

	@Inject(method = "clear", at = @At("TAIL"))
	public void celestetweaks$clearCompactHistory(boolean clearHistory, CallbackInfo ci) {
		Compacting.clear();
	}
}
