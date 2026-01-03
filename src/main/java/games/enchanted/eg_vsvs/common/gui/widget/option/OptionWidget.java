package games.enchanted.eg_vsvs.common.gui.widget.option;

import net.caffeinemc.mods.sodium.client.config.structure.Option;

public interface OptionWidget<T extends Option> {
    T getOption();
    void refreshValue();
    default void refreshVisual() {}
    void onChange(OnChange changeCallback);

    @FunctionalInterface
    interface OnChange {
        void changed();
    }
}
