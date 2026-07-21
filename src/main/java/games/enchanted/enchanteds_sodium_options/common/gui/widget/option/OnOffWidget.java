package games.enchanted.enchanteds_sodium_options.common.gui.widget.option;

import com.mojang.blaze3d.platform.InputConstants;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.AbstractWidgetPreventTooltipRender;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipContent;
import games.enchanted.enchanteds_sodium_options.common.gui.tooltip.TooltipConsumer;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.ResetOverlay;
import games.enchanted.enchanteds_sodium_options.common.util.ComponentUtil;
import net.caffeinemc.mods.sodium.client.config.structure.BooleanOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public class OnOffWidget extends Button implements OptionWidget<BooleanOption>, AbstractWidgetPreventTooltipRender {
    private static final Identifier DISABLED_SPRITE = Identifier.withDefaultNamespace("widget/button_disabled");

    protected final ResetOverlay resetOverlay;
    protected final TooltipContent tooltipContent;
    protected final TooltipConsumer tooltipConsumer;

    protected final BooleanOption option;
    protected boolean value;

    protected @Nullable OnChange onChange = null;

    public OnOffWidget(int x, int y, BooleanOption option, TooltipConsumer tooltipConsumer) {
        super(x, y, Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, option.getName(), (button) -> {}, DEFAULT_NARRATION);
        this.tooltipContent = new TooltipContent(ComponentUtil.createOptionTooltip(option), this.message, ComponentUtil.createPerformanceImpact(option));
        this.tooltipConsumer = tooltipConsumer;
        this.option = option;
        this.value = option.getValidatedValue();
        updateMessage();

        this.resetOverlay = new ResetOverlay(this, ResetOverlay.BUTTON_ICON_ID);
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
        this.setTooltip(this.tooltipContent.tooltipForNarration());
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if(this.isActive()) {
            this.extractDefaultSprite(graphics);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DISABLED_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
        }
        this.extractDefaultLabel(graphics.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));

        if(this.isHovered() && Minecraft.getInstance().hasShiftDown() && this.isActive()) {
            this.resetOverlay.extractRenderState(graphics, mouseX, mouseY, a);
        }

        if(this.isHoveredOrFocused()) {
            this.tooltipConsumer.submitTooltipContent(this.tooltipContent, this.isHovered(), this.isFocused(), this.getRectangle());
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(event.hasShiftDown() && event.button() == InputConstants.MOUSE_BUTTON_LEFT) {
            this.resetToDefault();
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        return super.mouseClicked(event, doubleClick);
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
        this.value = this.option.getValidatedValue();
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
