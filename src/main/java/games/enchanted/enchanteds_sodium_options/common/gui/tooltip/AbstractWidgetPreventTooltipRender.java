package games.enchanted.enchanteds_sodium_options.common.gui.tooltip;

import games.enchanted.enchanteds_sodium_options.common.config.ConfigOptions;

public interface AbstractWidgetPreventTooltipRender {
    default boolean enchanteds_sodium_options$preventTooltipRender() {
        return ConfigOptions.ALTERNATIVE_TOOLTIPS.getValue();
    }
}
