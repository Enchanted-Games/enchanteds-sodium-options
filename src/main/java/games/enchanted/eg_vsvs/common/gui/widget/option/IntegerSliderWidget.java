package games.enchanted.eg_vsvs.common.gui.widget.option;

import games.enchanted.eg_vsvs.common.gui.widget.extension.AbstractSliderButtonExtension;
import games.enchanted.eg_vsvs.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.api.config.option.SteppedValidator;
import net.caffeinemc.mods.sodium.client.config.structure.IntegerOption;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class IntegerSliderWidget extends AbstractSliderButton implements AbstractSliderButtonExtension, OptionWidget<IntegerOption> {
    final IntegerOption option;
    int realValue;

    @Nullable OnChange onChange = null;

    public IntegerSliderWidget(int x, int y, IntegerOption option) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, option.getName(), 0);
        this.setTooltip(Tooltip.create(ComponentUtil.createOptionTooltip(option)));
        this.option = option;
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
    }

    @Override
    protected void applyValue() {
        this.realValue = getOptionValue();
        this.option.modifyValue(this.realValue);
    }

    @Override
    protected void setValue(double value) {
        super.setValue(value);
        this.realValue = getOptionValue();
        this.option.modifyValue(this.realValue);
        if(this.onChange != null) {
            this.onChange.changed();
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
        this.value = getSliderValue(this.realValue);
    }

    @Override
    public Identifier eg_vsvs$getDisabledHandleSprite() {
        return Identifier.withDefaultNamespace("widget/slider");
    }


    @Override
    public IntegerOption getOption() {
        return this.option;
    }

    @Override
    public void refreshValue() {
        this.setValue(getSliderValue(this.option.getAppliedValue()));
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
