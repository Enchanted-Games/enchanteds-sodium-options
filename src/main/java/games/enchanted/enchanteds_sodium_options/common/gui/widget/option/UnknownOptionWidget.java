package games.enchanted.enchanteds_sodium_options.common.gui.widget.option;

import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import games.enchanted.enchanteds_sodium_options.common.mixin.accessor.sodium.OptionAccessor;
import net.caffeinemc.mods.sodium.client.config.structure.Option;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class UnknownOptionWidget extends AbstractButton {
    private static final WidgetSprites SPRITES = new WidgetSprites(
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID,"widget/unknown"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "widget/unknown_disabled"),
        Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "widget/unknown_highlighted")
    );

    public UnknownOptionWidget(int x, int y, Option option) {
        super(
            x,
            y,
            Button.DEFAULT_WIDTH,
            Button.DEFAULT_HEIGHT,
            // TODO: translation
            Component.literal("Unknown: " + ((OptionAccessor) option).enchanteds_sodium_options$getId())
        );
        this.setTooltip(Tooltip.create(Component.literal("Unknown option type '" + ((OptionAccessor) option).enchanteds_sodium_options$getId() + "'")));
    }

    @Override
    public void onPress(InputWithModifiers input) {
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            SPRITES.get(this.active, this.isHoveredOrFocused()),
            this.getX(),
            this.getY(),
            this.getWidth(),
            this.getHeight(),
            ARGB.white(this.alpha)
        );

        int left = this.getX() + 14;
        int right = this.getX() + this.getWidth() - 2;
        int top = this.getY();
        int bottom = this.getY() + this.getHeight();
        guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE)
            .acceptScrollingWithDefaultCenter(this.message, left, right, top, bottom);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
