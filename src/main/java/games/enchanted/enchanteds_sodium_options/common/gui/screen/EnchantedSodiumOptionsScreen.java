package games.enchanted.enchanteds_sodium_options.common.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.enchanteds_sodium_options.common.Logging;
import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import games.enchanted.enchanteds_sodium_options.common.compat.iris.IrisShaderButtonBuilder;
import games.enchanted.enchanteds_sodium_options.common.config.ConfigOptions;
import games.enchanted.enchanteds_sodium_options.common.config.option.ConfigOption;
import games.enchanted.enchanteds_sodium_options.common.gui.RefreshState;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipConsumer;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipContent;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipRenderHelper;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.option.*;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.tab.OptionListTabBar;
import games.enchanted.enchanteds_sodium_options.common.mixin.accessor.sodium.OptionAccessor;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.*;
import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class EnchantedSodiumOptionsScreen extends Screen implements TooltipConsumer {
    private static final Component TITLE = Component.translatable("options.videoTitle");
    private static final Component DONATION_BUTTON_TEXT = Component.translatable("sodium.options.buttons.donate");
    protected static final int FOOTER_BUTTON_WIDTH = 98;

    private static final int TOOLTIP_WIDTH = 320;
    private static final int TOOLTIP_PADDING = 4;
    private static final int TOOLTIP_SPACE_MARGIN_INLINE = 10;
    private static final int TOOLTIP_SPACE_MARGIN_BLOCK = HeaderAndFooterLayout.DEFAULT_HEADER_AND_FOOTER_HEIGHT;

    private static final List<ConfigOption<?>> REFRESH_SCREEN_OPTIONS = List.of(
        ConfigOptions.USE_TABS,
        ConfigOptions.COLLAPSE_SODIUM_OPTIONS,
        ConfigOptions.COLLAPSE_THRESHOLD
    );

    public static boolean forceSodiumScreen = false;

    protected final Screen parent;
    protected double initialScrollAmount;

    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    protected @Nullable VideoOptionsList optionsList;
    protected Map<String, OptionListTab> tabsByConfigId = new HashMap<>();
    protected final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget);
    protected @Nullable TabNavigationBar tabNavigationBar;

    protected @Nullable AbstractWidget undoButton;
    protected @Nullable AbstractWidget applyButton;
    protected @Nullable AbstractWidget doneButton;

    protected @Nullable AbstractWidget donateButton;
    protected @Nullable AbstractWidget shaderpacksButton;

    protected final ArrayList<OptionWidget<?>> optionWidgets = new ArrayList<>();

    @Nullable protected TooltipState tooltipState = null;
    protected final RefreshState refreshState;

    protected EnchantedSodiumOptionsScreen(Screen parent, Component title, double initialScrollAmount) {
        super(title);
        this.parent = parent;
        this.initialScrollAmount = initialScrollAmount;
        this.refreshState = new RefreshState(RefreshState.createInitialValuesMap(REFRESH_SCREEN_OPTIONS));
    }
    protected EnchantedSodiumOptionsScreen(Screen parent, double initialScrollAmount) {
        this(parent, TITLE, initialScrollAmount);
    }

    public static Screen createSodiumScreen(Screen parent) {
        forceSodiumScreen = true;
        Screen newScreen = VideoSettingsScreen.createScreen(parent);
        forceSodiumScreen = false;
        return newScreen;
    }

    public static Screen create(Screen parent) {
        return create(parent, 0.0d);
    }

    public static Screen create(Screen parent, double initialScrollAmount) {
        try {
            Screen screen = new EnchantedSodiumOptionsScreen(parent, initialScrollAmount);
            ConfigManager.CONFIG.resetAllOptionsFromBindings();
            return screen;
        } catch (Exception e) {
            return EnchantedSodiumOptionsScreen.createErrorScreen(e, parent);
        }
    }

    @Override
    protected void init() {
        try {
            ConfigManager.CONFIG.invalidateGlobalRebuildDependents();
            if(ConfigOptions.USE_TABS.getValue()) {
                this.createTabsLayout();
            } else {
                this.createSingleColumnLayout();
            }

            this.visitOptionsAndAddListeners();
            this.layout.visitWidgets(this::addRenderableWidget);

            this.updateFooterButtonState();
            this.repositionElements();
        }
        catch (Exception e) {
            Minecraft.getInstance().setScreen(createErrorScreen(e, this.parent));
        }
    }

    protected void createTabsLayout() {
        this.buildSodiumOptionWidgets();

        this.tabNavigationBar = new OptionListTabBar(this.width, this.tabManager, new ArrayList<>(this.tabsByConfigId.values()));
        this.addRenderableWidget(this.tabNavigationBar);
        this.tabNavigationBar.selectTab(0, false);

        this.createDonateAndShaderWidgets();
        this.createFooterWidgets();
    }

    protected void createSingleColumnLayout() {
        this.layout.addTitleHeader(this.title, this.font);
        int headerHeight = this.layout.getHeaderHeight();

        this.createDonateAndShaderWidgets();
        this.createFooterWidgets();

        this.optionsList = new VideoOptionsList(
            0,
            headerHeight,
            this.width,
            this.height - headerHeight - this.layout.getFooterHeight()
        );
        this.addRenderableWidget(this.optionsList);

        this.buildSodiumOptionWidgets();
    }

    protected void createDonateAndShaderWidgets() {
        this.donateButton = Button.builder(DONATION_BUTTON_TEXT, button -> {
            Util.getPlatform().openUri(ModConstants.SODIUM_DONATION);
        }).width(FOOTER_BUTTON_WIDTH).build();
        this.addRenderableWidget(this.donateButton);

        this.shaderpacksButton = IrisShaderButtonBuilder.getInstance().createShaderpacksButton(this, FOOTER_BUTTON_WIDTH);
        if(this.shaderpacksButton != null) {
            this.addRenderableWidget(this.shaderpacksButton);
        }
    }

    protected void createFooterWidgets() {
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
    }

    protected AbstractWidget buildDoneButtonWidget() {
        return Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(FOOTER_BUTTON_WIDTH).build();
    }

    protected void buildSodiumOptionWidgets() {
        List<ModOptions> modOptions = ConfigManager.CONFIG.getModOptions();

        for (ModOptions options : modOptions) {
            if(options.pages().isEmpty()) continue;

            var theme = options.theme();
            VideoOptionsList.ModInfo modInfo = new VideoOptionsList.ModInfo(
                options.configId(),
                Component.literal(options.name()),
                theme,
                VideoOptionsList.IconInfo.create(options.icon(), options.iconMonochrome())
            );

            Component modTitle = Component.literal(options.name());

            if(ConfigOptions.USE_TABS.getValue()) {
                String configId = options.configId();
                if(this.tabsByConfigId.containsKey(options.configId())) {
                    throw new IllegalStateException("Tried to create two tabs for the same config id '" + configId + "'!");
                }

                OptionListTab tab = new OptionListTab(modTitle, modInfo);
                this.tabsByConfigId.put(configId, tab);

                VideoOptionsList optionsList = tab.getOptionsList();

                optionsList.addSpacer(3, modInfo);

                this.buildPages(options.pages(), modInfo, optionsList);
            }
            else {
                if(this.optionsList == null) {
                    throw new IllegalStateException("optionList is null trying to build sodium option widgets for single column layout");
                }

                this.optionsList.addModTitle(
                    modTitle,
                    options.version(),
                    options.icon(),
                    options.iconMonochrome(),
                    modInfo
                );

                this.buildPages(options.pages(), modInfo, this.optionsList);
            }
        }
    }

    protected void buildPages(ImmutableList<Page> pages, VideoOptionsList.ModInfo modInfo, VideoOptionsList optionsList) {
        boolean allPagesCollapsed = true;
        List<OptionPage> collapsedOptionPages = new ArrayList<>();

        for (Page page : pages) {
            if(page instanceof ExternalPage(
                Component name, Consumer<Screen> currentScreenConsumer
            )) {
                optionsList.addBigOption(
                    Button.builder(ComponentUtil.appendEllipsis(name), button -> currentScreenConsumer.accept(this)).build(),
                    modInfo
                );
            }
            else if(page instanceof OptionPage optionPage) {
                AtomicInteger totalOptions = new AtomicInteger();
                optionPage.groups().forEach(optionGroup -> totalOptions.addAndGet(optionGroup.options().size()));

                final boolean shouldCollapseThisPage = modInfo.id().equals("sodium") ? ConfigOptions.COLLAPSE_SODIUM_OPTIONS.getValue() : true;

                if(totalOptions.get() > ConfigOptions.COLLAPSE_THRESHOLD.getValue() && shouldCollapseThisPage) {
                    // if this page is collapsed, skip and add it later
                    collapsedOptionPages.add(optionPage);
                    continue;
                }

                allPagesCollapsed = false;
                this.buildPageOptions(optionPage, new CollapsedPageInfo(false, false), modInfo, optionsList);
            }
            else {
                Logging.warn("Unknown page type. Class: {}, Name: {}", page.getClass().getCanonicalName(), page.name().getString());
            }
        }

        if(!collapsedOptionPages.isEmpty()) {
            if(!allPagesCollapsed) {
                optionsList.addCategoryHeader(Component.translatable("gui.enchanted_sodium_options.group.more"), modInfo);
            }

            for (OptionPage page : collapsedOptionPages) {
                this.buildPageOptions(page, new CollapsedPageInfo(true, collapsedOptionPages.size() == 1), modInfo, optionsList);
            }
        }
    }

    protected void buildPageOptions(OptionPage page, CollapsedPageInfo collapsedInfo, VideoOptionsList.ModInfo modInfo, VideoOptionsList optionsList) {
        if(!collapsedInfo.collapsed() || ConfigOptions.USE_TABS.getValue()) {
            optionsList.addCategoryHeader(page.name(), modInfo);
            this.buildGroupOptions(page.groups(), modInfo, optionsList);
            return;
        }

        AbstractWidget subPageButton = Button.builder(ComponentUtil.appendEllipsis(page.name()), button -> {
            this.minecraft.setScreen(new SubOptionsScreen(page, this, modInfo));
        }).build();

        if(collapsedInfo.onlyPageCollapsed()) {
            optionsList.addBigOption(subPageButton, modInfo);
        } else {
            optionsList.addOption(subPageButton, modInfo);
        }
    }

    protected void buildGroupOptions(ImmutableList<OptionGroup> groups, VideoOptionsList.ModInfo modInfo, VideoOptionsList optionsList) {
        for(OptionGroup group : groups) {
            if(group.name() != null) {
                optionsList.addGroupName(group.name(), modInfo);
            }
            var groupOptions = group.options();
            for (Option option : groupOptions) {
                optionsList.addOption(buildOptionWidget(option), modInfo);
            }
        }
    }

    public AbstractWidget buildOptionWidget(Option option) {
        switch (option) {
            case BooleanOption booleanOption -> {
                return new OnOffWidget(0, 0, booleanOption, this);
            }
            case IntegerOption integerOption -> {
                return new IntegerSliderWidget(0, 0, integerOption, this);
            }
            case ExternalButtonOption externalButtonOption -> {
                return Button.builder(option.getName(), button -> externalButtonOption.getCurrentScreenConsumer().accept(this))
                    .width(Button.DEFAULT_WIDTH)
                    .build();
            }
            case EnumOption<?> enumOption -> {
                return new EnumCyclerWidget<>(0, 0, enumOption, this);
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
        if(this.optionsList != null) {
            this.visitOptionList(this.optionsList);
        }
        for (Map.Entry<String, OptionListTab> tabEntry : this.tabsByConfigId.entrySet()) {
            this.visitOptionList(tabEntry.getValue().getOptionsList());
        }
    }

    private void visitOptionList(VideoOptionsList optionsList) {
        optionsList.visitChildren(widget -> {
            if(!(widget instanceof OptionWidget<?> optionWidget)) return;
            optionWidget.onChange(this::refreshOptionWidgetVisuals);
            this.optionWidgets.add(optionWidget);
        });
    }

    protected Screen getNonVideoOptionsParent() {
        return this.parent;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !this.hasPendingChanges();
    }

    private void undoChanges() {
        ConfigManager.CONFIG.resetAllOptionsFromBindings();
        this.refreshOptionWidgetValues();
    }

    private void saveChanges() {
        ConfigManager.CONFIG.applyAllOptions();
        if(this.refreshState.anyChanged(REFRESH_SCREEN_OPTIONS)) {
            Minecraft.getInstance().setScreen(create(
                this.getNonVideoOptionsParent(),
                this.optionsList == null ? 0.0d : this.optionsList.scrollAmount()
            ));
        }
        this.refreshOptionWidgetValues();
    }

    protected boolean hasPendingChanges() {
        return ConfigManager.CONFIG.anyOptionChanged();
    }

    protected void refreshOptionWidgetValues() {
        this.optionWidgets.forEach(OptionWidget::refreshValue);
        this.updateFooterButtonState();
    }

    protected void refreshOptionWidgetVisuals() {
        this.optionWidgets.forEach(OptionWidget::refreshVisual);
        this.updateFooterButtonState();
    }

    protected void updateFooterButtonState() {
        if(this.undoButton == null || this.applyButton == null || this.doneButton == null) return;
        if(this.hasPendingChanges()) {
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
    public void submitTooltipContent(TooltipContent content, boolean hovered, boolean focused, ScreenRectangle widgetRectangle) {
        boolean shouldShow = hovered || focused && minecraft.getLastInputType().isKeyboard();
        if(this.tooltipState == null && shouldShow) {
            this.tooltipState = new TooltipState(content, widgetRectangle);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if(ConfigOptions.USE_TABS.getValue()) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                CreateWorldScreen.TAB_HEADER_BACKGROUND,
                0,
                0,
                0.0f,
                0.0f,
                this.width,
                this.layout.getHeaderHeight(),
                16,
                16
            );
        }
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if(this.tooltipState != null) {
            TooltipRenderHelper.renderTooltip(
                this.extractTooltipRenderState(this.tooltipState),
                this.font,
                graphics,
                mouseX,
                mouseY,
                partialTick
            );

            this.tooltipState = null;
        }
    }

    protected TooltipRenderHelper.TooltipRenderState extractTooltipRenderState(TooltipState tooltipState) {
        final int width = Math.min(this.width - TOOLTIP_SPACE_MARGIN_INLINE, TOOLTIP_WIDTH);
        final int height = TooltipRenderHelper.calculateHeight(tooltipState.content(), this.font, width, TOOLTIP_PADDING);

        final var rectangle = tooltipState.widgetRectangle();
        final int tooltipAreaBottom = (this.height - TOOLTIP_SPACE_MARGIN_BLOCK);

        // align to top if overflowing the bottom of the screen
        int y = rectangle.bottom() + height > tooltipAreaBottom ?
            Math.max(TOOLTIP_SPACE_MARGIN_BLOCK, rectangle.top() - height - (TOOLTIP_PADDING * 2)) :
            rectangle.bottom()
        ;

        // last resort if still overflowing
        if(y + height > tooltipAreaBottom) {
            y = 0;
        }

        return new TooltipRenderHelper.TooltipRenderState(
            tooltipState.content(),
            (this.width / 2) - (width / 2),
            y,
            width,
            height,
            TOOLTIP_PADDING
        );
    }


    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        int headerHeight = this.layout.getHeaderHeight();

        if(this.shaderpacksButton != null) {
            this.shaderpacksButton.setPosition(
                this.width - this.shaderpacksButton.getWidth() - 8,
                (headerHeight / 2) - this.shaderpacksButton.getHeight() / 2
            );
        }

        if(this.donateButton != null) {
            this.donateButton.setPosition(
                8,
                (headerHeight / 2) - this.donateButton.getHeight() / 2
            );
        }

        if(this.tabNavigationBar != null) {
            this.tabNavigationBar.updateWidth(this.width);
            int tabAreaTop = this.tabNavigationBar.getRectangle().bottom();
            ScreenRectangle tabArea = new ScreenRectangle(0, tabAreaTop, this.width, this.height - this.layout.getFooterHeight() - tabAreaTop);
            this.layout.setHeaderHeight(tabAreaTop);
            this.layout.arrangeElements();
            this.tabManager.setTabArea(tabArea);
        }

        if(optionsList != null) {
            this.optionsList.setRectangle(
                this.width,
                this.height - headerHeight - this.layout.getFooterHeight(),
                0,
                headerHeight
            );
            this.optionsList.repositionElements();

            if(this.initialScrollAmount > 0) {
                this.optionsList.setScrollAmount(this.initialScrollAmount);
                this.initialScrollAmount = 0.0d;
            }
        }

        this.refreshOptionWidgetVisuals();
    }

    public static Screen createErrorScreen(@Nullable Exception e, Screen parent) {
        final String baseKey = "gui.enchanted_sodium_options.error_screen.body.";
        MutableComponent body = Component.empty().append(Component.translatable(baseKey + "1"));

        if(e != null) {
            Logging.error("Exception occurred while trying to show video setting screen.\n{}", e.toString());
            StackTraceElement[] traceElements = e.getStackTrace();
            StringBuilder builder = new StringBuilder("Stacktrace:");
            for (StackTraceElement traceElement : traceElements) {
                builder.append("\n").append("  ").append(traceElement);
            }
            Logging.error("{}", builder.toString());

            body.append("\n");
            if(e.getMessage() == null) {
                body.append(Component.literal("Check output log").withStyle(
                    style -> style.withColor(CommonColors.GRAY)
                ));
            } else {
                body.append(Component.literal(e.getMessage()).withStyle(
                    style -> style.withColor(CommonColors.GRAY)
                ));
            }
        }

        body.append("\n\n");
        body.append(Component.translatable(baseKey + "2").withStyle(style -> style.withBold(true)));
        body.append("\n\n");
        body.append(Component.translatable(baseKey + "3").withStyle(style ->
            style.withBold(true).withColor(ChatFormatting.RED)
        ));

        return new ConfirmScreen(
            confirmed -> {
                if(confirmed) {
                    Util.getPlatform().openUri(ModConstants.ISSUE_URI);
                } else {
                    Minecraft.getInstance().setScreen(EnchantedSodiumOptionsScreen.createSodiumScreen(parent));
                }
            },
            ComponentUtil.MOD_NAME,
            body,
            Component.translatable("gui.enchanted_sodium_options.report_button"),
            Component.translatable("gui.enchanted_sodium_options.sodium_screen")
        );
    }

    protected record CollapsedPageInfo(boolean collapsed, boolean onlyPageCollapsed) {
    }

    protected record TooltipState(TooltipContent content, ScreenRectangle widgetRectangle) {
    }
}
