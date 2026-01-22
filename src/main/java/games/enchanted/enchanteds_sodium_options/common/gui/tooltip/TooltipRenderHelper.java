package games.enchanted.enchanteds_sodium_options.common.gui.tooltip;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class TooltipRenderHelper {
    static final Identifier TOOLTIP_BACKGROUND = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "tooltip/background");

    public static void renderTooltip(TooltipRenderState state, Font font, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        List<FormattedCharSequence> bodyLines = state.content().getSplitBody(font, state.width() - (state.padding() * 2));
        Component impactText = state.content().getPerformanceImpact();

        final int valueHeight = (font.lineHeight * 2);
        final int effectiveHeight = state.height() + (state.padding() * 2);

        // background
        TooltipRenderUtil.renderTooltipBackground(
            graphics,
            state.x() + TooltipRenderUtil.PADDING_LEFT,
            state.y() + TooltipRenderUtil.PADDING_TOP,
            state.width() - TooltipRenderUtil.PADDING_LEFT - TooltipRenderUtil.PADDING_RIGHT,
            effectiveHeight - TooltipRenderUtil.PADDING_TOP - TooltipRenderUtil.PADDING_BOTTOM,
            null
        );
        if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_RSHIFT)) {
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                TOOLTIP_BACKGROUND,
                state.x(),
                state.y(),
                state.width(),
                effectiveHeight
            );
        }

        // value
        graphics.drawString(font, state.content().getOptionValue(), state.x() + state.padding(), state.y() + state.padding(), -1);

        // body and performance impact
        for (int i = 0; i < bodyLines.size(); i++) {
            FormattedCharSequence line = bodyLines.get(i);

            graphics.drawString(
                font,
                line,
                state.x() + state.padding(),
                state.y() + state.padding() + valueHeight + (font.lineHeight * i),
                -1
            );

            // performance impact
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

    public static int calculateHeight(TooltipContent content, Font font, int width, int padding) {
        List<FormattedCharSequence> bodyLines = content.getSplitBody(font, width - (padding * 2));
        Component impactText = content.getPerformanceImpact();

        final int valueHeight = (font.lineHeight * 2);
        final int impactHeight = impactText == null ? 0 : font.lineHeight;
        final int bodyHeight = font.lineHeight * bodyLines.size();
        return valueHeight + impactHeight + bodyHeight;

    }

    public record TooltipRenderState(TooltipContent content, int x, int y, int width, int height, int padding) {
    }
}
