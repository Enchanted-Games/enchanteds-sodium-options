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
                .addOptionGroup(createVisualGroup(builder))
            )
            .addPage(builder.createOptionPage()
                .setName(Component.translatable("gui.enchanted_sodium_options.group.behaviour"))
                .addOptionGroup(createLayoutGroup(builder))
            )
        ;
    }

    private static OptionGroupBuilder createSimpleGroup(ConfigBuilder builder, List<ConfigOption<?>> options) {
        var group = builder.createOptionGroup();
        for (ConfigOption<?> option : options) {
            group.addOption(option.createSodiumOption(builder, group));
        }
        return group;
    }

    private static OptionGroupBuilder createVisualGroup(ConfigBuilder builder) {
        var group = builder.createOptionGroup();
        group.addOption(ConfigOptions.ACCENT_BARS.createSodiumOption(builder, group));
        group.addOption(ConfigOptions.SHOW_MOD_ICONS.createSodiumOption(builder, group));
        group.addOption(
            ConfigOptions.COLOURED_HEADER_TEXT.createSodiumOption(builder, group).setEnabledProvider(
                configState -> !configState.readBooleanOption(ConfigOptions.USE_TABS.getConfigId()),
                ConfigOptions.USE_TABS.getConfigId()
            )
        );
        group.addOption(ConfigOptions.COLOURED_CATEGORY_TEXT.createSodiumOption(builder, group));
        group.addOption(
            ConfigOptions.COLOURED_TAB_UNDERLINES.createSodiumOption(builder, group).setEnabledProvider(
                configState -> configState.readBooleanOption(ConfigOptions.USE_TABS.getConfigId()),
                ConfigOptions.USE_TABS.getConfigId()
            )
        );
        return group;
    }

    private static OptionGroupBuilder createLayoutGroup(ConfigBuilder builder) {
        var group = builder.createOptionGroup();
        group.addOption(ConfigOptions.USE_TABS.createSodiumOption(builder, group));
        group.addOption(
            ConfigOptions.COLLAPSE_THRESHOLD.createSodiumOption(builder, group).setEnabledProvider(
                configState -> !configState.readBooleanOption(ConfigOptions.USE_TABS.getConfigId()),
                ConfigOptions.USE_TABS.getConfigId()
            )
        );
        group.addOption(
            ConfigOptions.COLLAPSE_SODIUM_OPTIONS.createSodiumOption(builder, group).setEnabledProvider(
                configState -> !configState.readBooleanOption(ConfigOptions.USE_TABS.getConfigId()),
                ConfigOptions.USE_TABS.getConfigId()
            )
        );
        return group;
    }
}
