package games.enchanted.enchanteds_sodium_options.common.gui.widget;

import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ResetOverlay {
    public static final Identifier BUTTON_ICON_ID = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "widget/reset_button");
    public static final Identifier SLIDER_ICON_ID = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "widget/reset_slider");
    private static final int WIDTH = 40;
    private static final int HEIGHT = 20;

    final AbstractWidget widget;
    final Identifier icon;

    public ResetOverlay(AbstractWidget widget, Identifier icon) {
        this.widget = widget;
        this.icon = icon;
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blitSprite(
            RenderPipelines.GUI_TEXTURED,
            this.icon,
            widget.getX() + widget.getWidth() - WIDTH,
            widget.getY(),
            WIDTH,
            HEIGHT
        );
    }
}
