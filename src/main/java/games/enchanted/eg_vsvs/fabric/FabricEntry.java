//? if fabric {
package games.enchanted.eg_vsvs.fabric;

import games.enchanted.eg_vsvs.common.ModEntry;
import net.fabricmc.api.ModInitializer;

public class FabricEntry implements ModInitializer {
    @Override
    public void onInitialize() {
        ModEntry.init();
    }
}
//?}