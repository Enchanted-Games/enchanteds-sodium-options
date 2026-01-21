package games.enchanted.enchanteds_sodium_options.common.gui.widget.option;

import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.AbstractWidgetPreventTooltipRender;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipContent;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipRenderer;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.structure.BooleanOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public class OnOffWidget extends Button implements OptionWidget<BooleanOption>, AbstractWidgetPreventTooltipRender {
    private static final Identifier DISABLED_SPRITE = Identifier.withDefaultNamespace("widget/button_disabled");

    final TooltipContent tooltipContent;
    final TooltipRenderer tooltipRenderer;

    final BooleanOption option;
    boolean value;

    @Nullable OnChange onChange = null;

    public OnOffWidget(int x, int y, BooleanOption option, TooltipRenderer tooltipRenderer) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, option.getName(), (button) -> {}, DEFAULT_NARRATION);
        this.tooltipContent = new TooltipContent(ComponentUtil.createOptionTooltip(option), this.message, ComponentUtil.createPerformanceImpact(option));
        this.tooltipRenderer = tooltipRenderer;
        this.option = option;
        this.value = option.getValidatedValue();
        updateMessage();
    }

    @Override
    public boolean isActive() {
        return this.option.isEnabled();
    }

    protected void updateMessage() {
        this.message = ComponentUtil.optionMessage(
            this.option.getName(),
            this.value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF,
            this.isActive(),
            this.option.hasChanged()
        );
        this.tooltipContent.setOptionValue(this.message);
        this.setTooltip(this.tooltipContent.createTooltip());
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(this.isActive()) {
            this.renderDefaultSprite(guiGraphics);
        } else {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, DISABLED_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        }
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
        if(this.isHoveredOrFocused()) {
            this.tooltipRenderer.submitTooltipContent(this.tooltipContent, this.isHovered(), this.isFocused(), this.getRectangle());
        }
    }

    @Override
    public void onPress(InputWithModifiers input) {
        this.value = !this.value;
        this.option.modifyValue(this.value);
        if(this.onChange != null) {
            this.onChange.changed();
        }
        updateMessage();
    }

    @Override
    public BooleanOption getOption() {
        return this.option;
    }

    @Override
    public void refreshValue() {
        this.value = option.getAppliedValue();
        updateMessage();
    }

    @Override
    public void onChange(OnChange changeCallback) {
        this.onChange = changeCallback;
    }

    @Override
    public void refreshVisual() {
        this.updateMessage();
    }
}
