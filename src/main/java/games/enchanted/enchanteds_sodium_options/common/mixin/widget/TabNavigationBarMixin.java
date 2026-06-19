package games.enchanted.enchanteds_sodium_options.common.mixin.widget;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import games.enchanted.enchanteds_sodium_options.common.gui.screen.OptionListTab;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.tab.ModInfoTabButton;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.tab.OptionListTabBar;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TabNavigationBar.class)
public class TabNavigationBarMixin {
    //? if minecraft: <= 26.1 {
    /*@WrapOperation(
        at = @At(value = "NEW", target = "(Lnet/minecraft/client/gui/components/tabs/TabManager;Lnet/minecraft/client/gui/components/tabs/Tab;II)Lnet/minecraft/client/gui/components/TabButton;"),
        method = "<init>"
    )
    private TabButton enchanted_sodium_options$createCustomTabButtonConditionally(
        TabManager tabManager,
        Tab tab,
        int width,
        int height,
        Operation<TabButton> original
    ) {
        if(!((Object) this instanceof OptionListTabBar optionListTabBar)) {
            return original.call(tabManager, tab, width, height);
        }
        if(tab instanceof OptionListTab optionListTab) {
            return new ModInfoTabButton(tabManager, tab, width, height, optionListTab.getModInfo());
        }
        return original.call(tabManager, tab, width, height);
    }
    *///? }
}
