package games.enchanted.enchanteds_sodium_options.common;

import games.enchanted.enchanteds_sodium_options.common.config.ConfigOptions;
import games.enchanted.enchanteds_sodium_options.common.config.option.ConfigOption;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.caffeinemc.mods.sodium.api.config.structure.OptionGroupBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

@ConfigEntryPointForge(ModConstants.MOD_ID)
public class SodiumConfigEntry implements ConfigEntryPoint {
    private static final int ACCENT_COLOUR = 0xff7b63;

    @Override
    public void registerConfigLate(ConfigBuilder builder) {
        builder.registerOwnModOptions()
            .setNonTintedIcon(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "icon.png"))
            .setColorTheme(builder.createColorTheme().setBaseThemeRGB(ACCENT_COLOUR))
            .addPage(builder.createOptionPage()
                .setName(Component.translatable("gui.enchanted_sodium_options.group.visual"))
                .addOptionGroup(createGroup(builder, List.of(
                    ConfigOptions.ACCENT_BARS,
                    ConfigOptions.SHOW_MOD_ICONS,
                    ConfigOptions.COLOURED_HEADER_TEXT,
                    ConfigOptions.COLOURED_CATEGORY_TEXT
                )))
            );
    }

    private static OptionGroupBuilder createGroup(ConfigBuilder builder, List<ConfigOption<?>> options) {
        var group = builder.createOptionGroup();
        for (ConfigOption<?> option : options) {
            group.addOption(option.createSodiumOption(builder, group));
        }
        return group;
    }
}
