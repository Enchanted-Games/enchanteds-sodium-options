package games.enchanted.enchanteds_sodium_options.common.mixin.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import games.enchanted.enchanteds_sodium_options.common.config.ConfigOptions;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.tab.ModInfoTabButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TabButton.class)
public abstract class TabButtonMixin extends AbstractWidget.WithInactiveMessage {
    public TabButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/TabButton;extractFocusUnderline(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"),
        method = "extractWidgetRenderState"
    )
    private void enchanted_sodium_options$modifyUnderlineColour(TabButton instance, GuiGraphicsExtractor graphics, Font font, int color, Operation<Void> original) {
        if((TabButton) (Object) this instanceof ModInfoTabButton modInfoTabButton && ConfigOptions.COLOURED_TAB_UNDERLINES.getValue()) {
            original.call(instance, graphics, font, this.active ? modInfoTabButton.getModAccent() : color);
            return;
        }
        original.call(instance, graphics, font, color);
    }
}
