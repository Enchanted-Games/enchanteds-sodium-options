package games.enchanted.eg_vsvs.common.gui.widget.option;

import games.enchanted.eg_vsvs.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.structure.BooleanOption;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.CommonColors;
import org.jspecify.annotations.Nullable;

public class OnOffWidget extends Button implements OptionWidget<BooleanOption> {
    final BooleanOption option;
    boolean value;

    @Nullable OnChange onChange = null;

    public OnOffWidget(int x, int y, BooleanOption option) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, option.getName(), (button) -> {}, DEFAULT_NARRATION);
        this.setTooltip(Tooltip.create(ComponentUtil.createOptionTooltip(option)));
        this.option = option;
        this.value = option.getValidatedValue();
        updateMessage();
    }

    @Override
    public boolean isActive() {
        return this.option.isEnabled();
    }

    protected void updateMessage() {
        this.message = CommonComponents.optionNameValue(
            this.option.getName(),
            this.value ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF
        ).withColor(this.isActive() ? -1 : CommonColors.LIGHT_GRAY);
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderDefaultSprite(guiGraphics);
        this.renderDefaultLabel(guiGraphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
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
