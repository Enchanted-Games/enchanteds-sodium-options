package games.enchanted.enchanteds_sodium_options.common.mixin.accessor;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TabNavigationBar.class)
public interface TabNavigationBarAccessor {
    @Accessor("tabButtons")
    ImmutableList<TabButton> enchanted_sodium_options$tabButtons();

    //? if minecraft: <= 26.1 {
    /*@Accessor("width")
    int enchanted_sodium_options$width();

    @Accessor("layout")
    LinearLayout enchanted_sodium_options$layout();
    *///? } else {
    @Accessor("layout")
    FrameLayout enchanted_sodium_options$layout();
    //? }
}
