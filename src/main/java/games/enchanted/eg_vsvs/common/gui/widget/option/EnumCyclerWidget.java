package games.enchanted.eg_vsvs.common.gui.widget.option;

import games.enchanted.eg_vsvs.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.structure.EnumOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.CommonColors;
import org.jspecify.annotations.Nullable;

public class EnumCyclerWidget<T extends Enum<T>> extends Button implements OptionWidget<EnumOption<T>> {
    final EnumOption<T> option;
    T value;
    final T[] enumValues;

    @Nullable OnChange onChange = null;

    public EnumCyclerWidget(int x, int y, EnumOption<T> option) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, option.getName(), (button) -> {}, DEFAULT_NARRATION);
        this.setTooltip(Tooltip.create(ComponentUtil.createOptionTooltip(option)));
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
        this.message = CommonComponents.optionNameValue(
            this.option.getName(),
            this.option.getElementName(this.value)
        ).withColor(this.isActive() ? -1 : CommonColors.LIGHT_GRAY);
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderDefaultSprite(guiGraphics);
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
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
