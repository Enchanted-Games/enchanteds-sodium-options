package games.enchanted.enchanteds_sodium_options.common.gui.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class TooltipContent {
    private final Component body;
    private Component optionValue;
    private @Nullable final Component performanceImpact;

    private int prevSplitWidth = 0;
    private @Nullable List<FormattedCharSequence> splitBody;

    public TooltipContent(Component body, Component optionValue,@Nullable  Component performanceImpact) {
        this.body = body;
        this.optionValue = optionValue;
        this.performanceImpact = performanceImpact;
    }

    public void setOptionValue(Component value) {
        this.optionValue = value;
    }

    public @Nullable Component getPerformanceImpact() {
        return this.performanceImpact;
    }

    public Component getOptionValue() {
        return this.optionValue;
    }

    public Component getBody() {
        return this.body;
    }

    public Tooltip tooltipForNarration() {
        MutableComponent message = this.body.copy();
        if(this.performanceImpact != null) message
            .append("\n")
            .append(this.performanceImpact);
        return Tooltip.create(message);
    }

    public List<FormattedCharSequence> getSplitBody(Font font, int width) {
        if(this.splitBody != null && this.prevSplitWidth == width) return this.splitBody;
        this.splitBody = font.split(this.getBody(), width);
        this.prevSplitWidth = width;
        return this.splitBody;
    }
}
