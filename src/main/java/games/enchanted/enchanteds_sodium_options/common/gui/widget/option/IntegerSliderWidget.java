package games.enchanted.enchanteds_sodium_options.common.gui.widget.option;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.AbstractWidgetPreventTooltipRender;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipConsumer;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipContent;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.ResetOverlay;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.extension.AbstractSliderButtonExtension;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class IntegerSliderWidget extends AbstractSliderButton implements AbstractSliderButtonExtension, OptionWidget<IntegerOption>, AbstractWidgetPreventTooltipRender {
    private static final Identifier DISABLED_HANDLE_SPRITE = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "widget/slider_handle_disabled");

    protected final ResetOverlay resetOverlay;
    protected final TooltipContent tooltipContent;
    protected final TooltipConsumer tooltipConsumer;

    protected final IntegerOption option;
    protected int realValue;
    protected int prevValue;

    protected @Nullable OnChange onChange = null;

    public IntegerSliderWidget(int x, int y, IntegerOption option, TooltipConsumer tooltipConsumer) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, option.getName(), 0);
        this.tooltipContent = new TooltipContent(ComponentUtil.createOptionTooltip(option), this.message, ComponentUtil.createPerformanceImpact(option));
        this.tooltipConsumer = tooltipConsumer;
        this.option = option;
        this.realValue = option.getAppliedValue();
        this.prevValue = this.realValue;
        this.setValue(this.getSliderValue());
        this.updateMessage();

        this.resetOverlay = new ResetOverlay(this, ResetOverlay.SLIDER_ICON_ID);
    }

    @Override
    public boolean isActive() {
        return this.option.isEnabled();
    }

    @Override
    protected void updateMessage() {
        this.message = ComponentUtil.optionMessage(
            this.option.getName(),
            this.option.formatValue(this.realValue),
            this.isActive(),
            this.option.hasChanged()
        );
        this.tooltipContent.setOptionValue(this.message);
        this.setTooltip(this.tooltipContent.tooltipForNarration());
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);

        if(this.isHovered() && Minecraft.getInstance().hasShiftDown() && this.isActive()) {
            this.resetOverlay.extractRenderState(graphics, mouseX, mouseY, a);
        }

        if(this.isHoveredOrFocused()) {
            this.tooltipConsumer.submitTooltipContent(this.tooltipContent, this.isHovered(), this.isFocused(), this.getRectangle());
        }
    }

    @Override
    protected void applyValue() {
        this.prevValue = this.realValue;
        this.realValue = getOptionValue();
        if(this.prevValue != this.realValue) {
            this.option.modifyValue(this.realValue);
            if(this.onChange != null) {
                this.onChange.changed();
            }
        }
        this.updateMessage();
    }

    private double getSliderValue() {
        return getSliderValue(this.option.getValidatedValue());
    }

    private double getSliderValue(int value) {
        SteppedValidator validator = this.option.getSteppedValidator();
        int min = validator.min();
        int max = validator.max();
        return Mth.clamp(((double) value - min) / (max - min), 0.0f, 1.0f);
    }

    private int getOptionValue() {
        SteppedValidator validator = this.option.getSteppedValidator();
        int min = validator.min();
        int max = validator.max();
        int step = validator.step();
        return min + step * (int) Math.round(this.value * (max - min) / step);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(event.hasShiftDown() && event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            this.resetToDefault();
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isSelection()) {
            this.canChangeValue = !this.canChangeValue;
            return true;
        }

        if (!this.canChangeValue) return false;

        boolean left = event.isLeft();
        boolean right = event.isRight();
        int stepValue = this.option.getSteppedValidator().step();
        if (left || right) {
            int direction = left ? -stepValue : stepValue;
            this.setValue(this.getSliderValue(this.realValue + direction));
            return true;
        }

        return false;
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        super.onRelease(event);
        this.setValue(getSliderValue(this.realValue));
    }

    @Override
    public Identifier enchanteds_sodium_options$getDisabledHandleSprite() {
        return DISABLED_HANDLE_SPRITE;
    }


    @Override
    public IntegerOption getOption() {
        return this.option;
    }

    @Override
    public void refreshValue() {
        this.realValue = this.option.getValidatedValue();
        this.prevValue = this.realValue;
        this.setValue(getSliderValue(this.realValue));
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
