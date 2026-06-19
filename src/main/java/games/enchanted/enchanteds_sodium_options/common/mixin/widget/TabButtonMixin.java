package games.enchanted.enchanteds_sodium_options.common.mixin.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import games.enchanted.enchanteds_sodium_options.common.config.ConfigOptions;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.tab.ModInfoTabButton;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if minecraft: <= 26.1 {
/*@Mixin(TabButton.class)
*///? } else {
import net.minecraft.client.gui.components.tabs.MenuTabBar;

@Mixin(MenuTabBar.MenuTabButton.class)
//? }
public abstract class TabButtonMixin extends AbstractWidget.WithInactiveMessage {
    public TabButtonMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @WrapOperation(
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"),
        //? if minecraft: <= 26.1 {
        /*method = "extractFocusUnderline"
        *///? } else {
        method = "renderFocusUnderline"
        //? }
    )
    private void enchanted_sodium_options$modifyUnderlineColour(GuiGraphicsExtractor instance, int x0, int y0, int x1, int y1, int col, Operation<Void> original) {
        if((TabButton) (Object) this instanceof ModInfoTabButton modInfoTabButton && ConfigOptions.COLOURED_TAB_UNDERLINES.getValue()) {
            int newCol = this.active ? modInfoTabButton.getModAccent() : col;
            int padding = ModInfoTabButton.PADDING + ModInfoTabButton.ICON_SIZE + ModInfoTabButton.PADDING;
            int width = this.getWidth() - padding * 2;
            int left = this.getX() + padding;
            int top = this.getY() + this.getHeight() - 2;
            original.call(instance, left, top, left + width, top + 1, newCol);
            return;
        }
        original.call(instance, x0, y0, x1, y1, col);
    }

    @WrapOperation(
        at = @At(
            value = "INVOKE",
            //? if minecraft: <= 26.1 {
            /*target = "Lnet/minecraft/client/gui/components/TabButton;extractLabel(Lnet/minecraft/client/gui/ActiveTextCollector;)V"
            *///? } else {
            target = "Lnet/minecraft/client/gui/components/tabs/MenuTabBar$MenuTabButton;renderLabel(Lnet/minecraft/client/gui/ActiveTextCollector;)V"
            //? }
        ),
        method = "extractWidgetRenderState"
    )
    private void enchanted_sodium_options$modifyLabel(
        //? if minecraft: <= 26.1 {
        /*TabButton instance, ActiveTextCollector output, Operation<Void> original, GuiGraphicsExtractor graphics
        *///? } else {
        MenuTabBar.MenuTabButton instance, ActiveTextCollector output, Operation<Void> original, GuiGraphicsExtractor graphics
        //? }
    ) {
        if((TabButton) (Object) this instanceof ModInfoTabButton modInfoTabButton) {
            modInfoTabButton.enchanted_sodium_options$extractLabel(graphics, output);
            return;
        }
        original.call(instance, output);
    }
}
