package games.enchanted.enchanteds_sodium_options.common.compat.iris;

import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class IrisShaderButtonBuilderImpl implements IrisShaderButtonBuilder {
    @Override
    public @Nullable AbstractWidget createShaderpacksButton(Screen parent, int width) {
        IrisApi api = IrisApi.getInstance();
        return Button
            .builder(
                Component.translatable(api.getMainScreenLanguageKey()),
                button -> Minecraft.getInstance().setScreen((Screen) api.openMainIrisScreenObj(parent))
            )
            .width(width)
        .build();
    }
}
