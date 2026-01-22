package games.enchanted.enchanteds_sodium_options.common.mixin.sodium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import games.enchanted.enchanteds_sodium_options.common.gui.screen.EnchantedSodiumOptionsScreen;
import net.caffeinemc.mods.sodium.client.gui.SodiumOptions;
import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VideoSettingsScreen.class, priority = 950)
public abstract class VideoSettingsScreenMixin extends Screen {
    protected VideoSettingsScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/gui/SodiumOptions;isReadOnly()Z"),
        method = "createScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/caffeinemc/mods/sodium/client/config/structure/OptionPage;)Lnet/minecraft/client/gui/screens/Screen;"
    )
    private static boolean wrapSodiumVideoScreenCtor(SodiumOptions instance, Operation<Boolean> original, Screen currentScreen, @Cancellable CallbackInfoReturnable<Screen> cir) {
        boolean configReadOnly = original.call(instance);
        if(EnchantedSodiumOptionsScreen.forceSodiumScreen) return configReadOnly;
        if(!configReadOnly) {
            cir.setReturnValue(EnchantedSodiumOptionsScreen.create(currentScreen));
            return false;
        }
        return true;
    }
}
