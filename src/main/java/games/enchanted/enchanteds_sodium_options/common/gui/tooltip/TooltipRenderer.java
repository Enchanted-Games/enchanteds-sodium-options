package games.enchanted.enchanteds_sodium_options.common.gui.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface TooltipRenderer {
    void submitTooltipContent(TooltipContent content, boolean force, ScreenRectangle widgetRectangle);

    default void renderCustomTooltip(TooltipRenderState state, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.fill(state.minX(), state.minY(), state.maxX(), state.maxY(), 0x8c000000);
        graphics.drawString(Minecraft.getInstance().font, state.content().getOptionValue(), 0, state.minY(), -1);
    }

    record TooltipRenderState(TooltipContent content, int minX, int minY, int maxX, int maxY) {
        int height() {
            return maxY() - minY();
        }
    }
}
