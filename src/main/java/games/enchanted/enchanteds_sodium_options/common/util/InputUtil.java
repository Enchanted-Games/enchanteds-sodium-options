package games.enchanted.enchanteds_sodium_options.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.enchanteds_sodium_options.common.PlatformHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

public class InputUtil {
    public static InputConstants.Key getKey(int key) {
        return getKey(key, 0);
    }

    public static InputConstants.Key getKey(int key, int scancode) {
        return InputConstants.getKey(new KeyEvent(key, scancode, 0));
    }

    public static boolean shouldShowDebugWidgetBound() {
        if(!PlatformHelper.isDevelopmentEnvironment()) return false;
        return InputConstants.isKeyDown(
            //? if minecraft: <= 26.2 {
            /*Minecraft.getInstance().getWindow(),
            *///? }
            InputConstants.KEY_RSHIFT
        );
    }
}
