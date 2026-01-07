package games.enchanted.enchanteds_sodium_options.common.config.option.integer;

import games.enchanted.enchanteds_sodium_options.common.config.option.IntOption;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.minecraft.network.chat.Component;

public class CollapseThresholdOption extends IntOption {
    public CollapseThresholdOption(Integer initialValue, Integer defaultValue, Range range, String jsonKey) {
        super(initialValue, defaultValue, range, jsonKey);
    }

    @Override
    protected Component formatValue(int value) {
        // TODO: translation
        if(value <= 0) return Component.literal("Collapse All");
        return super.formatValue(value);
    }
}
