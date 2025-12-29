//? if neoforge {
/*package games.enchanted.eg_vsvs.neoforge;

import games.enchanted.eg_vsvs.common.ModConstants;
import games.enchanted.eg_vsvs.common.ModEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/^*
 * This is the entry point for your mod's neoforge side.
 ^/
@Mod(value = ModConstants.MOD_ID, dist = Dist.CLIENT)
public class NeoForgeEntry {
    public NeoForgeEntry() {
        ModEntry.init();

        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class, () -> (client, parent) ->
                new ConfirmScreen(b -> Minecraft.getInstance().setScreen(parent), Component.literal(ModConstants.MOD_NAME + " Config Placeholder"), Component.empty())
        );
    }
}
*///?}