package games.enchanted.enchanteds_sodium_options.common.gui.screen;

import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class OptionListTab implements Tab {
    protected final Component title;
    private final VideoOptionsList optionsList;
    private final VideoOptionsList.ModInfo modInfo;
    private final LinearLayout layout;

    public OptionListTab(Component title, VideoOptionsList.ModInfo modInfo) {
        this.title = title;
        this.optionsList = new VideoOptionsList(0, 0, 0, 0);
        this.modInfo = modInfo;
        this.layout = LinearLayout.horizontal();
    }

    @Override
    public Component getTabTitle() {
        return this.title;
    }

    @Override
    public Component getTabExtraNarration() {
        return this.title;
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> childrenConsumer) {
        childrenConsumer.accept(this.optionsList);
    }

    @Override
    public void doLayout(ScreenRectangle screenRectangle) {
        this.optionsList.setRectangle(screenRectangle.width(), screenRectangle.height(), screenRectangle.left(), screenRectangle.top());
        this.optionsList.repositionElements();
    }

    //? if minecraft: >= 26.2 {
    @Override
    public Layout getLayout() {
        return this.layout;
    }
    //? }

    public VideoOptionsList getOptionsList() {
        return this.optionsList;
    }

    public VideoOptionsList.ModInfo getModInfo() {
        return this.modInfo;
    }
}
