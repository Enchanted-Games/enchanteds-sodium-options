package games.enchanted.eg_vsvs.common.util;

import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class ComponentUtil {
    public static final Component APPLY = Component.translatable("sodium.options.buttons.apply");
    public static final Component UNDO = Component.translatable("sodium.options.buttons.undo");

    public static Component createOptionTooltip(Option option) {
        if(option.getImpact() == null) return option.getTooltip();
        // TODO: translations
        return option.getTooltip().copy().append("\n\n").append(Component.literal("Performance impact ")).append(option.getImpact().getName());
    }

    public static Component optionMessage(Component optionName, Component value, boolean active, boolean modified) {
        return CommonComponents.optionNameValue(
            optionName,
                value.copy().withStyle(style -> style.withItalic(modified))
            ).withColor(active ? -1 : CommonColors.LIGHT_GRAY);
    }
}
