package games.enchanted.enchanteds_sodium_options.common.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.extension.AbstractSliderButtonExtension;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSliderButton.class)
public abstract class AbstractSliderButtonMixin extends AbstractWidget {
    public AbstractSliderButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractSliderButton;isHovered()Z"),
        method = "handleCursor"
    )
    public boolean wrapHoverCheck(AbstractSliderButton instance, Operation<Boolean> original, GuiGraphicsExtractor GuiGraphicsExtractor) {
        if(this instanceof AbstractSliderButtonExtension) {
            if(original.call(instance) && !this.isActive()) {
                GuiGraphicsExtractor.requestCursor(CursorTypes.NOT_ALLOWED);
                return false;
            }
        }
        return original.call(instance);
    }

    @WrapMethod(
        method = "getHandleSprite"
    )
    public Identifier wrapSpriteGetter(Operation<Identifier> original) {
        if(this instanceof AbstractSliderButtonExtension extension && !this.isActive()) {
            return extension.enchanteds_sodium_options$getDisabledHandleSprite();
        }
        return original.call();
    }
}
