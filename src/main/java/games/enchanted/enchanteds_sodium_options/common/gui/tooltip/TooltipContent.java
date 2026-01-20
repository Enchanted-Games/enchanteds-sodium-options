package games.enchanted.enchanteds_sodium_options.common.gui.tooltip;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

public class TooltipContent {
    final Component body;
    Component optionValue;
    @Nullable final Component performanceImpact;

    public TooltipContent(Component body, Component optionValue,@Nullable  Component performanceImpact) {
        this.body = body;
        this.optionValue = optionValue;
        this.performanceImpact = performanceImpact;
    }

    public void setOptionValue(Component value) {
        this.optionValue = value;
    }

    public Tooltip createTooltip() {
        MutableComponent tooltipContent = Component.empty()
            .append(this.optionValue.copy().withColor(0xd8d8da))
            .append("\n\n")
            .append(this.body)
        ;
        if(this.performanceImpact != null) {
            tooltipContent.append("\n\n").append(this.performanceImpact);
        }
        return Tooltip.create(tooltipContent, this.body);
    }

    public @Nullable Component getPerformanceImpact() {
        return performanceImpact;
    }

    public Component getOptionValue() {
        return optionValue;
    }

    public Component getBody() {
        return body;
    }
}
