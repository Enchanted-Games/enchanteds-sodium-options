package games.enchanted.enchanteds_sodium_options.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ScreenUtil {
    public static void setScreen(Minecraft minecraft, Screen screen) {
        //? if minecraft: <= 26.1 {
        /*minecraft.setScreen(screen);
         *///? } else {
        minecraft.gui.setScreen(screen);
        //? }
    }
}
