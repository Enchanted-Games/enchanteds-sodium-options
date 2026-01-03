package games.enchanted.eg_vsvs.common.gui;

import games.enchanted.eg_vsvs.common.Logging;
import games.enchanted.eg_vsvs.common.gui.widget.option.EnumCyclerWidget;
import games.enchanted.eg_vsvs.common.gui.widget.option.IntegerSliderWidget;
import games.enchanted.eg_vsvs.common.gui.widget.option.OnOffWidget;
import games.enchanted.eg_vsvs.common.gui.widget.option.OptionWidget;
import games.enchanted.eg_vsvs.common.gui.widget.scroll.VideoOptionsList;
import games.enchanted.eg_vsvs.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VideoOptionsScreen extends Screen {
    private static final int FOOTER_BUTTON_WIDTH = 98;

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    final Screen parent;
    VideoOptionsList optionsList;
    final ArrayList<OptionWidget<?>> optionWidgets = new ArrayList<>();

    public VideoOptionsScreen(Screen parent, Component title) {
        super(title);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);

        LinearLayout footerLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footerLayout.addChild(
            Button.builder(ComponentUtil.UNDO, button -> this.undoChanges()).width(FOOTER_BUTTON_WIDTH).build()
        );
        footerLayout.addChild(
            Button.builder(ComponentUtil.APPLY, button -> this.saveChanges()).width(FOOTER_BUTTON_WIDTH).build()
        );
        footerLayout.addChild(
            Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(FOOTER_BUTTON_WIDTH).build()
        );
        this.layout.visitWidgets(this::addRenderableWidget);

        int headerHeight = this.layout.getHeaderHeight();
        this.optionsList = new VideoOptionsList(
            0,
            headerHeight,
            this.width,
            this.height - headerHeight - this.layout.getFooterHeight()
        );
        this.addRenderableWidget(this.optionsList);

        List<ModOptions> modOptions = ConfigManager.CONFIG.getModOptions();

        for (ModOptions options : modOptions) {
            var theme = options.theme();

            this.optionsList.addHeader(Component.literal(options.name()).withColor(theme.themeLighter));

            var pages = options.pages();

            for (Page page : pages) {
                if(page instanceof ExternalPage(
                    Component name, Consumer<Screen> currentScreenConsumer
                )) {
                    this.optionsList.addBigOption(
                        Button.builder(name, button -> currentScreenConsumer.accept(this)).build()
                    );
                }
                else if(page instanceof OptionPage optionPage) {
                    this.optionsList.addHeader(page.name().copy().withColor(theme.theme));
                    buildPageOptions(optionPage);
                }
                else {
                    Logging.warn("Unknown page type. Class: {}, Name: {}", page.getClass().getCanonicalName(), page.name().getString());
                }
            }
        }

        this.visitOptionsAndAddListeners();
        this.repositionElements();
    }

    public void buildPageOptions(OptionPage page) {
        var groups = page.groups();

        for(OptionGroup group : groups) {
            var groupOptions = group.options();
            for (Option option : groupOptions) {
                this.optionsList.addOption(buildOptionWidget(option));
            }
        }
    }

    public AbstractWidget buildOptionWidget(Option option) {
        final AbstractWidget widget;
        if(option instanceof BooleanOption booleanOption) {
            widget = new OnOffWidget(0, 0, booleanOption);
        } else if(option instanceof IntegerOption integerOption) {
            widget = new IntegerSliderWidget(0, 0, integerOption);
        } else if(option instanceof ExternalButtonOption externalButtonOption) {
            widget = Button.builder(option.getName(), button -> {
                externalButtonOption.getCurrentScreenConsumer().accept(this);
            }).width(Button.DEFAULT_WIDTH).build();
        } else if(option instanceof EnumOption<?> enumOption) {
            widget = new EnumCyclerWidget<>(0, 0, enumOption);
        } else {
            Logging.warn("Unknown option type. Class: {}, Name: {}", option.getClass().getCanonicalName(), option.getName());
            return Button.builder(option.getName(), button -> {}).width(Button.DEFAULT_WIDTH).build();
        }

        return widget;
    }

    private void visitOptionsAndAddListeners() {
        this.optionsList.visitChildren(widget -> {
            if(!(widget instanceof OptionWidget<?> optionWidget)) return;
            optionWidget.onChange(this::refreshOptionWidgetVisuals);
            this.optionWidgets.add(optionWidget);
        });
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !ConfigManager.CONFIG.anyOptionChanged();
    }

    private void undoChanges() {
        ConfigManager.CONFIG.resetAllOptionsFromBindings();
        refreshOptionWidgetValues();
    }

    private void saveChanges() {
        ConfigManager.CONFIG.applyAllOptions();
        refreshOptionWidgetValues();
    }

    private void refreshOptionWidgetValues() {
        this.optionWidgets.forEach(OptionWidget::refreshValue);
    }

    private void refreshOptionWidgetVisuals() {
        this.optionWidgets.forEach(OptionWidget::refreshVisual);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if(optionsList != null) {
            int headerHeight = this.layout.getHeaderHeight();
            this.optionsList.setRectangle(
                this.width,
                this.height - headerHeight - this.layout.getFooterHeight(),
                0,
                headerHeight
            );
            this.optionsList.repositionElements();
        }
    }
}
