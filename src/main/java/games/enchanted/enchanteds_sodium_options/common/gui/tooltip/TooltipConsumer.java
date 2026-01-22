package games.enchanted.enchanteds_sodium_options.common.gui.tooltip;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface TooltipConsumer {
    void submitTooltipContent(TooltipContent content, boolean hovered, boolean focused, ScreenRectangle widgetRectangle);
}
