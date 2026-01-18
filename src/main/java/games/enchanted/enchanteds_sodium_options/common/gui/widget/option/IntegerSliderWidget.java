package games.enchanted.enchanteds_sodium_options.common.gui.widget.option;

import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.extension.AbstractSliderButtonExtension;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.util.TooltipUtil;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class IntegerSliderWidget extends AbstractSliderButton implements AbstractSliderButtonExtension, OptionWidget<IntegerOption> {
    private static final Identifier DISABLED_HANDLE_SPRITE = Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "widget/slider_handle_disabled");

    final Component tooltipContent;
    final IntegerOption option;
    int realValue;
    int prevValue;

    @Nullable OnChange onChange = null;

    public IntegerSliderWidget(int x, int y, IntegerOption option) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, option.getName(), 0);
        this.tooltipContent = ComponentUtil.createOptionTooltip(option);
        this.option = option;
        this.realValue = option.getAppliedValue();
        this.prevValue = this.realValue;
        this.setValue(this.getSliderValue());
        this.updateMessage();
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
        TooltipUtil.appendMessageToWidgetTooltip(this, this.message, this.tooltipContent);
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
        this.realValue = option.getAppliedValue();
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
