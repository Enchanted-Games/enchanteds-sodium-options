package games.enchanted.enchanteds_sodium_options.common.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import games.enchanted.enchanteds_sodium_options.common.config.ConfigOptions;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.StatefulOptionBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class IntOption extends ConfigOption<Integer> {
    final Range range;

    public IntOption(Integer initialValue, Integer defaultValue, Range range, String jsonKey) {
        super(initialValue, defaultValue, jsonKey);
        this.range = range;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void fromJson(JsonObject json) {
        Integer value = json.has(getJsonKey()) ? json.get(getJsonKey()).getAsInt() : getDefaultValue();
        this.setValueOrPending(value);
    }

    @Override
    public StatefulOptionBuilder<Integer> createSodiumOption(ConfigBuilder builder, OptionGroupBuilder group) {
        String optionId = this.getJsonKey();
        return builder.createIntegerOption(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, optionId))
            .setName(ComponentUtil.componentForOption(optionId))
            .setTooltip(ComponentUtil.componentForOption(optionId, true))
            .setStorageHandler(ConfigOptions::saveConfig)
            .setBinding(
                value -> {
                    this.setPendingValue(value);
                    this.applyPendingValue();
                },
                this::getValue
            )
            .setDefaultValue(this.getDefaultValue())
            .setRange(this.range)
            .setValueFormatter(i -> Component.literal("" + i));
    }
}
