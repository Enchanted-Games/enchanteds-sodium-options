package games.enchanted.enchanteds_sodium_options.common.gui.screen;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.enchanteds_sodium_options.common.Logging;
import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import games.enchanted.enchanteds_sodium_options.common.compat.iris.IrisShaderButtonBuilder;
import games.enchanted.enchanteds_sodium_options.common.config.ConfigOptions;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipContent;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipRenderer;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.option.*;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import games.enchanted.enchanteds_sodium_options.common.mixin.accessor.sodium.OptionAccessor;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.ConfigManager;
import net.caffeinemc.mods.sodium.client.config.structure.*;
import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class VideoOptionsScreen extends Screen implements TooltipRenderer {
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
    @Nullable AbstractWidget shaderpacksButton;

    final ArrayList<OptionWidget<?>> optionWidgets = new ArrayList<>();
    ScreenRectangle topHalfRectangle = new ScreenRectangle(0, 0, this.width, this.height / 2);

    @Nullable protected TooltipRenderInfo pendingTooltip = null;

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
        try {
            return new VideoOptionsScreen(parent);
        } catch (Exception e) {
            return VideoOptionsScreen.createErrorScreen(e, parent);
        }
    }

    @Override
    protected void init() {
        try {
            this.layout.addTitleHeader(this.title, this.font);
            int headerHeight = this.layout.getHeaderHeight();

            this.donateButton = Button.builder(DONATION_BUTTON_TEXT, button -> {
                Util.getPlatform().openUri("https://caffeinemc.net/donate");
            }).width(FOOTER_BUTTON_WIDTH).build();
            this.addRenderableWidget(this.donateButton);

            this.shaderpacksButton = IrisShaderButtonBuilder.getInstance().createShaderpacksButton(this, FOOTER_BUTTON_WIDTH);
            if(this.shaderpacksButton != null) {
                this.addRenderableWidget(this.shaderpacksButton);
            }

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

            this.buildSodiumOptionWidgets();

            this.visitOptionsAndAddListeners();
            this.layout.visitWidgets(this::addRenderableWidget);

            this.updateFooterButtonState();
            this.repositionElements();
        }
        catch (Exception e) {
            Minecraft.getInstance().setScreen(createErrorScreen(e, this.parent));
        }
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
            if(options.pages().isEmpty()) continue;

            var theme = options.theme();
            VideoOptionsList.ModInfo modInfo = new VideoOptionsList.ModInfo(
                options.configId(),
                Component.literal(options.name()),
                theme,
                VideoOptionsList.IconInfo.create(options.icon(), options.iconMonochrome())
            );

            this.optionsList.addModTitle(
                Component.literal(options.name()),
                options.version(),
                options.icon(),
                options.iconMonochrome(),
                modInfo
            );

            this.buildPages(options.pages(), modInfo);
        }
    }

    protected void buildPages(ImmutableList<Page> pages, VideoOptionsList.ModInfo modInfo) {
        if(this.optionsList == null) {
            throw new IllegalStateException("optionList is null trying to build pages");
        }

        boolean allPagesCollapsed = true;
        List<OptionPage> collapsedOptionPages = new ArrayList<>();

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
                AtomicInteger totalOptions = new AtomicInteger();
                optionPage.groups().forEach(optionGroup -> totalOptions.addAndGet(optionGroup.options().size()));

                final boolean shouldCollapseThisPage = modInfo.id().equals("sodium") ? ConfigOptions.COLLAPSE_SODIUM_OPTIONS.getValue() : true;

                if(totalOptions.get() > ConfigOptions.COLLAPSE_THRESHOLD.getValue() && shouldCollapseThisPage) {
                    // if this page is collapsed, skip and add it later
                    collapsedOptionPages.add(optionPage);
                    continue;
                }

                allPagesCollapsed = false;
                this.buildPageOptions(optionPage, new CollapsedPageInfo(false, false), modInfo);
            }
            else {
                Logging.warn("Unknown page type. Class: {}, Name: {}", page.getClass().getCanonicalName(), page.name().getString());
            }
        }

        if(!collapsedOptionPages.isEmpty()) {
            if(!allPagesCollapsed) {
                this.optionsList.addCategoryHeader(Component.translatable("gui.enchanted_sodium_options.group.more"), modInfo);
            }

            for (OptionPage page : collapsedOptionPages) {
                this.buildPageOptions(page, new CollapsedPageInfo(true, collapsedOptionPages.size() == 1), modInfo);
            }
        }
    }

    protected void buildPageOptions(OptionPage page, CollapsedPageInfo collapsedInfo, VideoOptionsList.ModInfo modInfo) {
        if(this.optionsList == null) {
            throw new IllegalStateException("optionList is null trying to build page options");
        }

        if(!collapsedInfo.collapsed()) {
            this.optionsList.addCategoryHeader(page.name(), modInfo);
            this.buildGroupOptions(page.groups(), modInfo);
            return;
        }

        AbstractWidget subPageButton = Button.builder(ComponentUtil.appendEllipsis(page.name()), button -> {
            this.minecraft.setScreen(new SubVideoOptionsScreen(page, this, modInfo));
        }).build();

        if(collapsedInfo.onlyPageCollapsed()) {
            this.optionsList.addBigOption(subPageButton, modInfo);
        } else {
            this.optionsList.addOption(subPageButton, modInfo);
        }
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
        if(this.optionsList == null) return;
        this.optionsList.visitChildren(widget -> {
            if(!(widget instanceof OptionWidget<?> optionWidget)) return;
            optionWidget.onChange(this::anyOptionChanged);
            this.optionWidgets.add(optionWidget);
        });
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if(this.pendingTooltip != null) {
            boolean positionAtBottom = this.pendingTooltip.widgetRectangle().overlaps(this.topHalfRectangle);

            guiGraphics.fill(0, positionAtBottom ? this.height / 2 : 0, this.width, positionAtBottom ? this.height : this.height / 2, 0x33000000);
            guiGraphics.drawString(this.font, this.pendingTooltip.content().getOptionValue(), 0, this.height / 2, -1);
            this.pendingTooltip = null;
        }
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
        this.refreshOptionWidgetValues();
    }

    private boolean hasPendingChanges() {
        return ConfigManager.CONFIG.anyOptionChanged();
    }

    private void refreshOptionWidgetValues() {
        this.optionWidgets.forEach(OptionWidget::refreshValue);
        this.updateFooterButtonState();
    }

    protected void anyOptionChanged() {
        this.updateFooterButtonState();
        this.optionWidgets.forEach(OptionWidget::refreshVisual);
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
    public void submitTooltipContent(TooltipContent content, boolean force, ScreenRectangle widgetRectangle) {
        if(!ConfigOptions.ALTERNATIVE_TOOLTIPS.getValue()) return;
        if(this.pendingTooltip == null || force) {
            this.pendingTooltip = new TooltipRenderInfo(content, widgetRectangle);
        }
    }


    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        int headerHeight = this.layout.getHeaderHeight();
        this.topHalfRectangle = new ScreenRectangle(0, 0, this.width, this.height / 2);

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
            body.append(Component.literal(e.getMessage()).withStyle(
                style -> style.withColor(CommonColors.GRAY)
            ));
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
                    Minecraft.getInstance().setScreen(VideoOptionsScreen.createSodiumScreen(parent));
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

    protected record TooltipRenderInfo(TooltipContent content, ScreenRectangle widgetRectangle) {
    }
}
