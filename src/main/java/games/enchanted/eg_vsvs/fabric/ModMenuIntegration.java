//? if fabric {
package games.enchanted.eg_vsvs.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import games.enchanted.eg_vsvs.common.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> new ConfirmScreen(b -> Minecraft.getInstance().setScreen(parent), Component.literal(ModConstants.MOD_NAME + " Config Placeholder"), Component.empty());
    }
}
//?}