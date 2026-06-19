package games.enchanted.enchanteds_sodium_options.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.AbstractWidgetPreventTooltipRender;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    //? if minecraft: <= 26.1 {
    /*@WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/WidgetTooltipHolder;refreshTooltipForNextRenderPass(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIZZLnet/minecraft/client/gui/navigation/ScreenRectangle;)V"),
        method = "extractRenderState"
    )
    private void preventTooltipRender(WidgetTooltipHolder instance, GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, boolean hovering, boolean focused, ScreenRectangle screenRectangle, Operation<Void> original) {
        if(((Object) this) instanceof AbstractWidgetPreventTooltipRender extension && extension.enchanteds_sodium_options$preventTooltipRender()) {
            return;
        }
        original.call(instance, GuiGraphicsExtractor, mouseX, mouseY, hovering, focused, screenRectangle);
    }
    *///? } else {
    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;extractTooltipForNextRenderPass(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"),
        method = "extractRenderState"
    )
    private void preventTooltipRender(AbstractWidget instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, Operation<Void> original) {
        if(((Object) this) instanceof AbstractWidgetPreventTooltipRender extension && extension.enchanteds_sodium_options$preventTooltipRender()) {
            return;
        }
        original.call(instance, graphics, mouseX, mouseY);
    }
    //? }
}
