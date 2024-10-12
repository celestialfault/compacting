package dev.celestialfault.compacting.config

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

object ConfigGui {
	fun generateScreen(parent: Screen?): Screen {
		return YetAnotherConfigLib.createBuilder()
			.title(Text.translatable("compacting.title"))
			.category(ConfigCategory.createBuilder()
				.name(Text.translatable("compacting.title"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("compacting.enable"))
					.description(OptionDescription.of(Text.translatable("compacting.enable.desc")))
					.binding(true, { Config.enabled }, { Config.enabled = it })
					.controller(TickBoxControllerBuilder::create)
					.build())
				.option(Option.createBuilder<Int>()
					.name(Text.translatable("compacting.timeout"))
					.description(OptionDescription.of(Text.translatable("compacting.timeout.desc.line1")
						.append("\n\n")
						.append(Text.translatable("compacting.timeout.desc.line2"))))
					.binding(60, { Config.timeout }, { Config.timeout = it })
					.controller {
						IntegerSliderControllerBuilder.create(it)
							.range(30, 120)
							.step(1)
							.formatValue { Text.translatable("compacting.seconds", it) }
					}
					.build())
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("compacting.remove_blank"))
					.description(OptionDescription.of(Text.translatable("compacting.remove_blank.desc")))
					.binding(false, { Config.removeBlank }, { Config.removeBlank = it })
					.controller(TickBoxControllerBuilder::create)
					.build())
				.build())
			.save(Config::save)
			.build()
			.generateScreen(parent)
	}
}
