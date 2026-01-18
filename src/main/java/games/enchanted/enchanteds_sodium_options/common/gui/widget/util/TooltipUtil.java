package games.enchanted.enchanteds_sodium_options.common.gui.widget.util;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class TooltipUtil {
    public static void appendMessageToWidgetTooltip(AbstractWidget widget, Component message, Component tooltip) {
        Component tooltipContent = Component.empty()
            .append(message.copy().withColor(0xd8d8da))
            .append("\n\n")
            .append(tooltip);
        widget.setTooltip(Tooltip.create(tooltipContent, tooltip));
    }
}
