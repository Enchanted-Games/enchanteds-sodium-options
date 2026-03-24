//? if fabric {
package games.enchanted.enchanteds_sodium_options.fabric;

import games.enchanted.enchanteds_sodium_options.common.ModEntry;
import net.fabricmc.api.ModInitializer;

public class FabricEntry implements ModInitializer {
    @Override
    public void onInitialize() {
        ModEntry.init();
    }
}
//?}