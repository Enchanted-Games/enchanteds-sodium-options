package games.enchanted.enchanteds_sodium_options.common.gui.screen;

import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class OptionListTab implements Tab {
    protected final Component title;
    private final VideoOptionsList optionsList;

    public OptionListTab(Component title) {
        this.title = title;
        this.optionsList = new VideoOptionsList(0, 0, 0, 0);
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

    public VideoOptionsList getOptionsList() {
        return this.optionsList;
    }
}
