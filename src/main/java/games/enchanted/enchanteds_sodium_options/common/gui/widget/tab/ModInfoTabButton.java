package games.enchanted.enchanteds_sodium_options.common.gui.widget.tab;

import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;

public class ModInfoTabButton extends TabButton {
    final VideoOptionsList.ModInfo modInfo;

    public ModInfoTabButton(TabManager tabManager, Tab tab, int width, int height, VideoOptionsList.ModInfo modInfo) {
        super(tabManager, tab, width, height);
        this.modInfo = modInfo;
    }

    public int getModAccent() {
        return this.modInfo.theme().theme;
    }
}
