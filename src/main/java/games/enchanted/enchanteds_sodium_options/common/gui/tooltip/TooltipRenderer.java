package games.enchanted.enchanteds_sodium_options.common.gui.tooltip;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface TooltipRenderer {
    void submitTooltipContent(TooltipContent content, boolean force, ScreenRectangle widgetRectangle);
}
