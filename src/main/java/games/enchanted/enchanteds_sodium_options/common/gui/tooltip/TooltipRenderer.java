package games.enchanted.enchanteds_sodium_options.common.gui.tooltip;

import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public interface TooltipRenderer {
    static final Identifier TOOLTIP_BACKGROUND = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "tooltip/background");

    void submitTooltipContent(TooltipContent content, boolean hovered, boolean focused, ScreenRectangle widgetRectangle);

    default void renderCustomTooltip(TooltipRenderState state, Font font, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        List<FormattedCharSequence> bodyLines = state.content().getSplitBody(font, state.width() - (state.padding() * 2));
        Component impactText = state.content().getPerformanceImpact();

        final int valueHeight = (font.lineHeight * 2);
        final int impactHeight = impactText == null ? 0 : font.lineHeight;
        final int bodyHeight = font.lineHeight * (bodyLines.size() + 1);
        final int totalHeight = valueHeight + impactHeight + bodyHeight;
        final int effectiveHeight = Math.min(totalHeight, state.maxHeight());

        // background
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            TOOLTIP_BACKGROUND,
            state.x(),
            state.y(),
            state.width(),
            effectiveHeight
        );

        // value
        graphics.drawString(font, state.content().getOptionValue(), state.x() + state.padding(), state.y() + state.padding(), -1);

        // body and impact text
        for (int i = 0; i < bodyLines.size(); i++) {
            FormattedCharSequence line = bodyLines.get(i);

            graphics.drawString(
                font,
                line,
                state.x() + state.padding(),
                state.y() + state.padding() + valueHeight + (font.lineHeight * i),
                -1
            );

            if(i == bodyLines.size() - 1 && impactText != null) {
                graphics.drawString(
                    font,
                    impactText,
                    state.x() + state.padding(),
                    state.y() + state.padding() + valueHeight + (font.lineHeight * (i + 1)),
                    -1
                );
            }
        }
    }

    record TooltipRenderState(TooltipContent content, int x, int y, int width, int maxHeight, int padding) {

    }
}
