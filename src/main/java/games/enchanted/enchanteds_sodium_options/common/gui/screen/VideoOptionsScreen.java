package games.enchanted.enchanteds_sodium_options.common.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.enchanteds_sodium_options.common.Logging;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.option.*;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import games.enchanted.enchanteds_sodium_options.common.mixin.accessor.sodium.OptionAccessor;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.*;
import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class VideoOptionsScreen extends Screen {
    private static final Component TITLE = Component.translatable("options.videoTitle");
    private static final Component DONATION_BUTTON_TEXT = Component.translatable("sodium.options.buttons.donate");
    protected static final int FOOTER_BUTTON_WIDTH = 98;

    public static boolean forceSodiumScreen = false;

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    final Screen parent;
    @Nullable VideoOptionsList optionsList;
    @Nullable AbstractWidget undoButton;
    @Nullable AbstractWidget applyButton;
    @Nullable AbstractWidget doneButton;
    @Nullable AbstractWidget donateButton;

    final ArrayList<OptionWidget<?>> optionWidgets = new ArrayList<>();

    protected VideoOptionsScreen(Screen parent, Component title) {
        super(title);
        this.parent = parent;
    }
    protected VideoOptionsScreen(Screen parent) {
        this(parent, TITLE);
    }

    public static Screen createSodiumScreen(Screen parent) {
        forceSodiumScreen = true;
        Screen newScreen = VideoSettingsScreen.createScreen(parent);
        forceSodiumScreen = false;
        return newScreen;
    }

    public static Screen create(Screen parent) {
        return new VideoOptionsScreen(parent);
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
            this.buildDoneButtonWidget()
        );

        this.optionsList = new VideoOptionsList(
            0,
            headerHeight,
            this.width,
            this.height - headerHeight - this.layout.getFooterHeight()
        );
        this.addRenderableWidget(this.optionsList);

        buildSodiumOptionWidgets();

        this.visitOptionsAndAddListeners();
        this.layout.visitWidgets(this::addRenderableWidget);

        this.updateFooterButtonState();
        this.repositionElements();
    }

    protected AbstractWidget buildDoneButtonWidget() {
        return Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(FOOTER_BUTTON_WIDTH).build();
    }

    protected void buildSodiumOptionWidgets() {
        if(this.optionsList == null) {
            throw new IllegalStateException("optionList is null trying to build sodium option widgets");
        }

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
                        Button.builder(ComponentUtil.appendEllipsis(name), button -> currentScreenConsumer.accept(this)).build(),
                        modInfo
                    );
                }
                else if(page instanceof OptionPage optionPage) {
                    buildPageOptions(optionPage, modInfo);
                }
                else {
                    Logging.warn("Unknown page type. Class: {}, Name: {}", page.getClass().getCanonicalName(), page.name().getString());
                }
            }
        }
    }

    protected void buildPageOptions(OptionPage page, VideoOptionsList.ModInfo modInfo) {
        if(this.optionsList == null) {
            throw new IllegalStateException("optionList is null trying to build page options");
        }

        this.optionsList.addCategoryHeader(page.name(), modInfo);

        ImmutableList<OptionGroup> groups = page.groups();

        AtomicInteger totalOptions = new AtomicInteger();
        page.groups().forEach(optionGroup -> totalOptions.addAndGet(optionGroup.options().size()));

        if(totalOptions.get() > 6 && !modInfo.id().equals("sodium")) {
            this.optionsList.addBigOption(
                Button.builder(ComponentUtil.appendEllipsis(page.name()), button -> {
                    this.minecraft.setScreen(new SubVideoOptionsScreen(page, this, modInfo));
                }).build(),
                modInfo
            );
            return;
        }

        buildGroupOptions(groups, modInfo);
    }

    protected void buildGroupOptions(ImmutableList<OptionGroup> groups, VideoOptionsList.ModInfo modInfo) {
        if(this.optionsList == null) {
            throw new IllegalStateException("optionList is null trying to build group options");
        }

        for(OptionGroup group : groups) {
            if(group.name() != null) {
                this.optionsList.addGroupName(group.name(), modInfo);
            }
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
        if(this.optionsList == null) return;
        this.optionsList.visitChildren(widget -> {
            if(!(widget instanceof OptionWidget<?> optionWidget)) return;
            optionWidget.onChange(this::anyOptionChanged);
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

    protected void anyOptionChanged() {
        updateFooterButtonState();
        this.optionWidgets.forEach(OptionWidget::refreshVisual);
    }

    protected void updateFooterButtonState() {
        if(this.undoButton == null || this.applyButton == null || this.doneButton == null) return;
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
    public boolean keyPressed(KeyEvent event) {
        if(this.shouldOpenSodiumScreenOnKeybind() && event.hasAltDown() && event.key() == InputConstants.KEY_P) {
            Minecraft.getInstance().setScreen(createSodiumScreen(this.parent));
            return true;
        }
        return super.keyPressed(event);
    }

    protected boolean shouldOpenSodiumScreenOnKeybind() {
        return true;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        int headerHeight = this.layout.getHeaderHeight();

        if(this.donateButton != null) {
            this.donateButton.setPosition(
                this.width - this.donateButton.getWidth() - 8,
                (headerHeight / 2) - this.donateButton.getHeight() / 2
            );
        }

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
