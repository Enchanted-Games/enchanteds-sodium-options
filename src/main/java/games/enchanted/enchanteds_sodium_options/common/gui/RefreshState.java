package games.enchanted.enchanteds_sodium_options.common.gui;

import games.enchanted.enchanteds_sodium_options.common.config.option.ConfigOption;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record RefreshState(Map<Identifier, Object> initialOptionValues) {
    public boolean anyChanged(List<ConfigOption<?>> options) {
        Map<Identifier, Object> current = createInitialValuesMap(options);
        for (Map.Entry<Identifier, Object> entry : current.entrySet()) {
            if(this.initialOptionValues.get(entry.getKey()) == null) continue;
            Object initialValue = this.initialOptionValues.get(entry.getKey());
            if(!initialValue.equals(entry.getValue())) return true;
        }
        return false;
    }

    public static Map<Identifier, Object> createInitialValuesMap(List<ConfigOption<?>> options) {
        Map<Identifier, Object> map = new HashMap<>();
        for (ConfigOption<?> option : options) {
            map.put(option.getConfigId(), option.getValue());
        }
        return map;
    }
}
