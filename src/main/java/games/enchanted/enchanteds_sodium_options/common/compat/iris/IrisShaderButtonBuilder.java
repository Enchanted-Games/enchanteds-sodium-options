package games.enchanted.enchanteds_sodium_options.common.compat.iris;

import games.enchanted.enchanteds_sodium_options.common.PlatformHelper;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public interface IrisShaderButtonBuilder {
    default boolean irisPresent() {
        return false;
    }

    default Button.OnPress createClickCallback(Screen parent) {
        return null;
    }

    default Component getMessage() {
        return null;
    }

    static IrisShaderButtonBuilder getInstance() {
        if(PlatformHelper.isModLoaded("iris")) {
            return new IrisShaderButtonBuilderImpl();
        }
        return new IrisShaderButtonBuilder() {};
    }
}
