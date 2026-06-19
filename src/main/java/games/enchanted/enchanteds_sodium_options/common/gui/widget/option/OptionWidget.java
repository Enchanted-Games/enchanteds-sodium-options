package games.enchanted.enchanteds_sodium_options.common.gui.widget.option;

import net.caffeinemc.mods.sodium.client.config.structure.Option;

public interface OptionWidget<T extends Option> {
    T getOption();
    void refreshValue();
    default void refreshVisual() {}
    void onChange(OnChange changeCallback);

    default void resetToDefault() {
        this.getOption().resetToDefault();
        this.refreshValue();
        this.refreshVisual();
    }

    @FunctionalInterface
    interface OnChange {
        void changed();
    }
}
