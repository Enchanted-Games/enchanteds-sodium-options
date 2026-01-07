package games.enchanted.enchanteds_sodium_options.common.util;

import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class ComponentUtil {
    public static final Component APPLY = Component.translatable("sodium.options.buttons.apply");
    public static final Component UNDO = Component.translatable("sodium.options.buttons.undo");
    public static final Component MOD_NAME = Component.translatable("gui.enchanted_sodium_options.name");

    public static final String OPTION_PREFIX = "gui.enchanted_sodium_options.option.";
    public static final String OPTION_TOOLTIP_SUFFIX = ".tooltip";

    public static Component createOptionTooltip(Option option) {
        if(option.getImpact() == null) return option.getTooltip();
        return option.getTooltip().copy().append("\n\n")
            .append(Component.translatable("sodium.options.performance_impact_string", option.getImpact().getName())
                .withStyle(ChatFormatting.GRAY)
            );
    }

    public static Component optionMessage(Component optionName, Component value, boolean active, boolean modified) {
        return CommonComponents.optionNameValue(
            optionName,
                value.copy().withStyle(style -> style.withItalic(modified))
            ).withColor(active ? -1 : CommonColors.LIGHT_GRAY);
    }

    public static Component componentForOption(String optionId) {
        return componentForOption(optionId, false);
    }
    public static Component componentForOption(String optionId, boolean tooltip) {
        return Component.translatable(OPTION_PREFIX + optionId + (tooltip ? OPTION_TOOLTIP_SUFFIX : ""));
    }

    public static Component appendEllipsis(Component base) {
        if(base.getString().endsWith("..")) return base;
        return Component.translatable("gui.enchanted_sodium_options.ellipsis", base);
    }
}
