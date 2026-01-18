package games.enchanted.enchanteds_sodium_options.common.compat.iris;

import games.enchanted.enchanteds_sodium_options.common.PlatformHelper;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.jspecify.annotations.Nullable;

public interface IrisShaderButtonBuilder {
    default @Nullable AbstractWidget createShaderpacksButton(Screen parent, int width) {
        return null;
    }

    static IrisShaderButtonBuilder getInstance() {
        if(PlatformHelper.isModLoaded("iris")) {
            return new IrisShaderButtonBuilderImpl();
        }
        return new IrisShaderButtonBuilder() {};
    }
}
