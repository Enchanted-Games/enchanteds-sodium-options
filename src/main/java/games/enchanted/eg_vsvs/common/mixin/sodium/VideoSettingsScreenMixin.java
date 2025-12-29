package games.enchanted.eg_vsvs.common.mixin.sodium;

import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoSettingsScreen.class)
public class VideoSettingsScreenMixin {
    @Shadow
    @Final
    private Screen prevScreen;

    @Inject(
        at = @At("TAIL"),
        method = "init"
    )
    private void afterScreenInit(CallbackInfo info) {
        Minecraft.getInstance().setScreen(new ConfirmScreen(b -> {Minecraft.getInstance().setScreen(prevScreen);}, Component.literal(""), Component.empty()));
    }
}
