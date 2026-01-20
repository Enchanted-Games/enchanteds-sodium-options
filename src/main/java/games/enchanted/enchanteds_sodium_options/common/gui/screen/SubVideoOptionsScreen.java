package games.enchanted.enchanteds_sodium_options.common.gui.screen;

import com.google.common.collect.ImmutableList;
import games.enchanted.enchanteds_sodium_options.common.gui.widget.scroll.VideoOptionsList;
import net.caffeinemc.mods.sodium.client.config.structure.OptionGroup;
import net.caffeinemc.mods.sodium.client.config.structure.OptionPage;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;

public class SubVideoOptionsScreen extends VideoOptionsScreen {
    final VideoOptionsScreen videoParent;
    final OptionPage page;
    final VideoOptionsList.ModInfo modInfo;

    public SubVideoOptionsScreen(OptionPage page, VideoOptionsScreen parent, VideoOptionsList.ModInfo modInfo) {
        super(parent, page.name());
        this.videoParent = parent;
        this.page = page;
        this.modInfo = modInfo;
    }

    @Override
    protected AbstractWidget buildDoneButtonWidget() {
        return Button.builder(CommonComponents.GUI_BACK, button -> this.onClose()).width(FOOTER_BUTTON_WIDTH).build();
    }

    @Override
    protected void buildSodiumOptionWidgets() {
        if(this.optionsList == null) {
            throw new IllegalStateException("optionList is null trying to build page options");
        }

        ImmutableList<OptionGroup> groups = page.groups();
        this.buildGroupOptions(groups, this.modInfo);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.videoParent.anyOptionChanged();
    }

    @Override
    protected boolean shouldOpenSodiumScreenOnKeybind() {
        return false;
    }
}
