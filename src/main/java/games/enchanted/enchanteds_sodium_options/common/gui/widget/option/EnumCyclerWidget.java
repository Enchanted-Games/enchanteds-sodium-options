package games.enchanted.enchanteds_sodium_options.common.gui.widget.option;

import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.AbstractWidgetPreventTooltipRender;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipContent;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipRenderer;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.structure.EnumOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import org.jspecify.annotations.Nullable;

public class EnumCyclerWidget<T extends Enum<T>> extends Button implements OptionWidget<EnumOption<T>>, AbstractWidgetPreventTooltipRender {
    final TooltipContent tooltipContent;
    final TooltipRenderer tooltipRenderer;

    final EnumOption<T> option;
    T value;
    final T[] enumValues;

    @Nullable OnChange onChange = null;

    public EnumCyclerWidget(int x, int y, EnumOption<T> option, TooltipRenderer tooltipRenderer) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, option.getName(), (button) -> {}, DEFAULT_NARRATION);
        this.tooltipContent = new TooltipContent(ComponentUtil.createOptionTooltip(option), this.message, ComponentUtil.createPerformanceImpact(option));
        this.tooltipRenderer = tooltipRenderer;
        this.option = option;
        this.value = option.getValidatedValue();
        this.enumValues = this.getOption().enumClass.getEnumConstants();
        this.updateMessage();
    }

    @Override
    public boolean isActive() {
        return this.option.isEnabled();
    }

    protected void updateMessage() {
        this.message = ComponentUtil.optionMessage(
            this.option.getName(),
            this.option.getElementName(this.value),
            this.isActive(),
            this.option.hasChanged()
        );
        this.tooltipContent.setOptionValue(this.message);
        this.setTooltip(this.tooltipContent.createTooltip());
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderDefaultSprite(guiGraphics);
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
        if(this.isHoveredOrFocused()) {
            this.tooltipRenderer.submitTooltipContent(this.tooltipContent, this.isHovered(), this.getRectangle());
        }
    }

    @Override
    public void onPress(InputWithModifiers input) {
        boolean reverse = input.hasShiftDown();

        int startIndex = 0;
        for (; startIndex < this.enumValues.length; startIndex++) {
            if (this.enumValues[startIndex] == this.value) {
                break;
            }
        }

        int index = startIndex;
        do {
            index = (index + (reverse ? this.enumValues.length - 1 : 1)) % this.enumValues.length;
            this.value = this.enumValues[index];
        } while (!this.option.isValueAllowed(this.value));

        this.option.modifyValue(this.value);
        if(this.onChange != null) {
            this.onChange.changed();
        }
        this.updateMessage();
    }

    @Override
    public EnumOption<T> getOption() {
        return this.option;
    }

    @Override
    public void refreshValue() {
        this.value = this.option.getAppliedValue();
        this.updateMessage();
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
