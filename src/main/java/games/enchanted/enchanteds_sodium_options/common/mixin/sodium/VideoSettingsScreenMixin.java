package games.enchanted.enchanteds_sodium_options.common.mixin.sodium;

import games.enchanted.enchanteds_sodium_options.common.gui.VideoOptionsScreen;
import games.enchanted.enchanteds_sodium_options.common.util.InputUtil;
import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public class VideoSettingsScreenMixin extends Screen {
    @Shadow
    @Final
    private Screen prevScreen;

    protected VideoSettingsScreenMixin(Component title) {
        super(title);
    }

    @Inject(
        at = @At("TAIL"),
        method = "init"
    )
    private void afterScreenInit(CallbackInfo info) {
        if(!InputUtil.shouldShowDebugWidgetBound()) {
            Minecraft.getInstance().setScreen(new VideoOptionsScreen(prevScreen));
        }
    }
}
