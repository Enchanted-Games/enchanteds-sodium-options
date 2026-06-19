package games.enchanted.enchanteds_sodium_options.common.gui.widget.tab;

import com.google.common.collect.ImmutableList;
import games.enchanted.enchanteds_sodium_options.common.gui.screen.OptionListTab;
import games.enchanted.enchanteds_sodium_options.common.mixin.accessor.TabNavigationBarAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.Layout;
import org.jspecify.annotations.Nullable;

import java.util.List;

//? if minecraft: >= 26.2 {
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.layouts.FrameLayout;
//? }

public class OptionListTabBar
    //? if minecraft: <= 26.1 {
    /*extends TabNavigationBar
    *///? } else {
    extends MenuTabBar
    //? }
{
    protected final int INLINE_MARGIN = 24;
    protected final int TAB_INLINE_PADDING = 10;
    protected final int MIN_TAB_WIDTH = 100;
    protected final int MAX_SCROLL_AREA = 1200;

    protected int horizontalScrollAmount = 0;
    protected int maxHorizontalScrollAmount = 0;

    public OptionListTabBar(int width, TabManager tabManager, Iterable<Tab> tabs) {
        //? if minecraft: <= 26.1 {
        /*super(width, tabManager, tabs);
        *///? } else {
        super(0, 0, width, 24, tabManager, createButtons(tabs, tabManager), ImmutableList.copyOf(tabs));
        //? }
        for(TabButton tab : ((TabNavigationBarAccessor) this).enchanted_sodium_options$tabButtons()) {
            tab.setTooltip(Tooltip.create(tab.tab().getTabTitle()));
        }
    }

    private static ImmutableList<TabButton> createButtons(Iterable<Tab> tabs, TabManager tabManager) {
        ImmutableList.Builder<TabButton> tabButtonsBuilder = ImmutableList.builder();

        for(Tab tab : tabs) {
            if(tab instanceof OptionListTab optionListTab) {
                tabButtonsBuilder.add(new ModInfoTabButton(tabManager, tab, 0, 24, optionListTab.getModInfo()));
            }
        }

        return tabButtonsBuilder.build();
    }



    @Override
    public void arrangeElements(
        //? if minecraft: >= 26.2 {
        int width
        //? }
    ) {
        final ImmutableList<TabButton> tabs = ((TabNavigationBarAccessor) this).enchanted_sodium_options$tabButtons();
        //? if minecraft: <= 26.1 {
        /*final int width = ((TabNavigationBarAccessor) this).enchanted_sodium_options$width();
        *///? }
        final int scaledWidth = Math.min(width, MAX_SCROLL_AREA);
        final int paddedWidth = scaledWidth - (INLINE_MARGIN * 2);

        int totalTabsWidth = 0;
        for (TabButton tab : tabs) {
            int newWidth = Math.max(Minecraft.getInstance().font.width(tab.getMessage()) + TAB_INLINE_PADDING * 2, MIN_TAB_WIDTH);
            totalTabsWidth += newWidth;
            tab.setWidth(newWidth);
        }

        // resize tabs to fill space if they aren't overflowing the edges
        if(totalTabsWidth < paddedWidth) {
            totalTabsWidth = paddedWidth;
            final int tabWidth = paddedWidth / tabs.size();

            List<TabButton> bigTabs = tabs.stream().filter(t -> t.getWidth() >= tabWidth).toList();
            final int leftoverWidth = paddedWidth - bigTabs.stream().mapToInt(AbstractWidget::getWidth).sum();

            List<TabButton> smallTabs = tabs.stream().filter(t -> t.getWidth() < tabWidth).toList();
            if(!smallTabs.isEmpty()) {
                final int tabWidthForSmall = leftoverWidth / smallTabs.size();
                for (TabButton tab : smallTabs) {
                    tab.setWidth(tabWidthForSmall);
                }
            }
        }

        Layout layout = ((TabNavigationBarAccessor) this).enchanted_sodium_options$layout();
        layout.arrangeElements();
        layout.setY(0);
        layout.setX(Math.max((width - totalTabsWidth) / 2, INLINE_MARGIN));

        this.horizontalScrollAmount = 0;
        this.maxHorizontalScrollAmount = Math.max(0, totalTabsWidth - paddedWidth);

        if(this.getFocused() instanceof TabButton tab) {
            this.scrollIntoView(tab);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY <= ((TabNavigationBarAccessor) this).enchanted_sodium_options$layout().getHeight();
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        this.setHorizontalScrollAmount(this.horizontalScrollAmount - (int) (scrollX * 15) - (int) (scrollY * 15));
        return true;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        super.setFocused(focused);
        if(focused instanceof TabButton tab) {
            this.scrollIntoView(tab);
        }
    }

    public void setHorizontalScrollAmount(int scrollOffset) {
        Layout layout = ((TabNavigationBarAccessor) this).enchanted_sodium_options$layout();

        layout.setX(layout.getX() + this.horizontalScrollAmount);
        this.horizontalScrollAmount = Math.clamp(scrollOffset, 0, this.maxHorizontalScrollAmount);
        layout.setX(layout.getX() - this.horizontalScrollAmount);
    }

    protected void scrollIntoView(TabButton button) {
        final int width =
            //? if minecraft: <= 26.1 {
            /*((TabNavigationBarAccessor) this).enchanted_sodium_options$width();
            *///? } else {
            this.width;
            //? }

        if (button.getX() < INLINE_MARGIN) {
            this.setHorizontalScrollAmount(this.horizontalScrollAmount - (INLINE_MARGIN - button.getX()));
            return;
        }

        if (button.getX() + button.getWidth() > width - INLINE_MARGIN) {
            this.setHorizontalScrollAmount(this.horizontalScrollAmount + (button.getX() + button.getWidth() - (width - INLINE_MARGIN)));
        }
    }
}
