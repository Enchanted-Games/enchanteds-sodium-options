package games.enchanted.enchanteds_sodium_options.common.gui.widget.tab;

import games.enchanted.enchanteds_sodium_options.common.config.ConfigOptions;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.renderer.RenderPipelines;

//? if minecraft: >= 26.2 {
import net.minecraft.client.gui.components.tabs.MenuTabBar;
//?}

public class ModInfoTabButton
    //? if minecraft: <= 26.1 {
    /*extends TabButton
    *///? } else {
    extends MenuTabBar.MenuTabButton
    //? }
{
    public static final int ICON_SIZE = 16;
    public static final int PADDING = 5;

    final VideoOptionsList.ModInfo modInfo;

    public ModInfoTabButton(TabManager tabManager, Tab tab, int width, int height, VideoOptionsList.ModInfo modInfo) {
        super(tabManager, tab, width, height);
        this.modInfo = modInfo;
    }

    public int getModAccent() {
        return this.modInfo.theme().theme;
    }

    public void enchanted_sodium_options$extractLabel(GuiGraphicsExtractor graphics, ActiveTextCollector output) {
        boolean hasIcon = ConfigOptions.SHOW_MOD_ICONS.getValue() && this.modInfo.iconInfo() != null;

        int left = this.getX() + PADDING;
        int top = this.getY();
        int textTop = top + (this.isSelected() ? 0 : 3);
        int iconSpace = (hasIcon ? ICON_SIZE + 4 : 1);
        int textLeft = left + iconSpace;
        int right = this.getX() + this.getWidth() - iconSpace - PADDING;
        int bottom = this.getY() + this.getHeight();

        output.acceptScrollingWithDefaultCenter(this.getMessage(), textLeft, right, textTop, bottom);

        if(hasIcon) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                this.modInfo.iconInfo().icon(),
                left,
                top + (this.isSelected() ? 4 : 6),
                0f,
                0f,
                ICON_SIZE,
                ICON_SIZE,
                ICON_SIZE,
                ICON_SIZE,
                !this.modInfo.iconInfo().monochrome() ? -1 : this.modInfo.theme().themeLighter
            );
        }
    }
}
