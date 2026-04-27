package games.enchanted.enchanteds_sodium_options.common.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class IrisShaderButtonBuilderImpl implements IrisShaderButtonBuilder {
    @Override
    public boolean irisPresent() {
        return true;
    }

    public Button.OnPress createClickCallback(Screen parent) {
        IrisApi api = IrisApi.getInstance();
        return button -> Minecraft.getInstance().setScreen((Screen) api.openMainIrisScreenObj(parent));
    }

    @Override
    public Component getMessage() {
        IrisApi api = IrisApi.getInstance();
        return Component.translatable(api.getMainScreenLanguageKey());
    }
}
