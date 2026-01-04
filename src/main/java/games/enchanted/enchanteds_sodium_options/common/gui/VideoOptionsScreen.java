package games.enchanted.enchanteds_sodium_options.common.gui;

import games.enchanted.enchanteds_sodium_options.common.Logging;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.option.*;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import games.enchanted.enchanteds_sodium_options.common.mixin.accessor.sodium.OptionAccessor;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.*;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VideoOptionsScreen extends Screen {
    private static final Component TITLE = Component.translatable("options.videoTitle");
    private static final Component DONATION_BUTTON_TEXT = Component.translatable("sodium.options.buttons.donate");
    private static final int FOOTER_BUTTON_WIDTH = 98;

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    final Screen parent;
    VideoOptionsList optionsList;
    AbstractWidget undoButton;
    AbstractWidget applyButton;
    AbstractWidget doneButton;
    AbstractWidget donateButton;

    final ArrayList<OptionWidget<?>> optionWidgets = new ArrayList<>();

    public VideoOptionsScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);
        int headerHeight = this.layout.getHeaderHeight();

        this.donateButton = Button.builder(DONATION_BUTTON_TEXT, button -> {
            Util.getPlatform().openUri("https://caffeinemc.net/donate");
        }).width(FOOTER_BUTTON_WIDTH).build();
        this.addRenderableWidget(this.donateButton);

        LinearLayout footerLayout = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        this.undoButton = footerLayout.addChild(
            Button.builder(ComponentUtil.UNDO, button -> this.undoChanges()).width(FOOTER_BUTTON_WIDTH).build()
        );
        this.applyButton = footerLayout.addChild(
            Button.builder(ComponentUtil.APPLY, button -> this.saveChanges()).width(FOOTER_BUTTON_WIDTH).build()
        );
        this.doneButton = footerLayout.addChild(
            Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(FOOTER_BUTTON_WIDTH).build()
        );

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
            VideoOptionsList.ModInfo modInfo = new VideoOptionsList.ModInfo(options.configId(), theme);

            this.optionsList.addModTitle(
                Component.literal(options.name()),
                Component.literal("v" + options.version()),
                options.icon(),
                options.iconMonochrome(),
                modInfo
            );

            var pages = options.pages();

            for (Page page : pages) {
                if(page instanceof ExternalPage(
                    Component name, Consumer<Screen> currentScreenConsumer
                )) {
                    this.optionsList.addBigOption(
                        Button.builder(name, button -> currentScreenConsumer.accept(this)).build(),
                        modInfo
                    );
                }
                else if(page instanceof OptionPage optionPage) {
                    this.optionsList.addHeader(page.name(), modInfo);
                    buildPageOptions(optionPage, modInfo);
                }
                else {
                    Logging.warn("Unknown page type. Class: {}, Name: {}", page.getClass().getCanonicalName(), page.name().getString());
                }
            }
        }

        this.visitOptionsAndAddListeners();
        this.layout.visitWidgets(this::addRenderableWidget);

        this.updateFooterButtonState();
        this.repositionElements();
    }

    public void buildPageOptions(OptionPage page, VideoOptionsList.ModInfo modInfo) {
        var groups = page.groups();

        // TODO: display group names
        for(OptionGroup group : groups) {
            var groupOptions = group.options();
            for (Option option : groupOptions) {
                this.optionsList.addOption(buildOptionWidget(option), modInfo);
            }
        }
    }

    public AbstractWidget buildOptionWidget(Option option) {
        switch (option) {
            case BooleanOption booleanOption -> {
                return new OnOffWidget(0, 0, booleanOption);
            }
            case IntegerOption integerOption -> {
                return new IntegerSliderWidget(0, 0, integerOption);
            }
            case ExternalButtonOption externalButtonOption -> {
                return Button.builder(option.getName(), button -> externalButtonOption.getCurrentScreenConsumer().accept(this))
                    .width(Button.DEFAULT_WIDTH)
                    .build();
            }
            case EnumOption<?> enumOption -> {
                return new EnumCyclerWidget<>(0, 0, enumOption);
            }
            default -> {
                Logging.warn(
                    "Unknown option type. Class: {}, Name: {}, id: {}",
                    option.getClass().getCanonicalName(),
                    option.getName().getString(),
                    ((OptionAccessor) option).enchanteds_sodium_options$getId()
                );
                return new UnknownOptionWidget(0, 0, option);
            }
        }
    }

    private void visitOptionsAndAddListeners() {
        if(!this.optionWidgets.isEmpty()) {
            throw new IllegalStateException("visitOptionsAndAddListeners was called while optionWidgets list was not empty");
        }
        this.optionsList.visitChildren(widget -> {
            if(!(widget instanceof OptionWidget<?> optionWidget)) return;
            optionWidget.onChange(this::optionChanged);
            this.optionWidgets.add(optionWidget);
        });
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return !hasPendingChanges();
    }

    private void undoChanges() {
        ConfigManager.CONFIG.resetAllOptionsFromBindings();
        refreshOptionWidgetValues();
    }

    private void saveChanges() {
        ConfigManager.CONFIG.applyAllOptions();
        refreshOptionWidgetValues();
    }

    private boolean hasPendingChanges() {
        return ConfigManager.CONFIG.anyOptionChanged();
    }

    private void refreshOptionWidgetValues() {
        this.optionWidgets.forEach(OptionWidget::refreshValue);
        this.updateFooterButtonState();
    }

    private void optionChanged() {
        updateFooterButtonState();
        this.optionWidgets.forEach(OptionWidget::refreshVisual);
    }

    private void updateFooterButtonState() {
        if(hasPendingChanges()) {
            this.undoButton.active = true;
            this.applyButton.active = true;
            this.doneButton.active = false;
        } else {
            this.undoButton.active = false;
            this.applyButton.active = false;
            this.doneButton.active = true;
        }
    }


    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        int headerHeight = this.layout.getHeaderHeight();
        this.donateButton.setPosition(
            this.width - this.donateButton.getWidth() - 8,
            (headerHeight / 2) - this.donateButton.getHeight() / 2
        );

        if(optionsList != null) {
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
