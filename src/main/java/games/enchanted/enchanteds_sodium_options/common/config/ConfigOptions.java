package games.enchanted.enchanteds_sodium_options.common.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import games.enchanted.enchanteds_sodium_options.common.Logging;
import games.enchanted.enchanteds_sodium_options.common.ModConstants;
import games.enchanted.enchanteds_sodium_options.common.PlatformHelper;
import games.enchanted.enchanteds_sodium_options.common.config.option.BoolOption;
import games.enchanted.enchanteds_sodium_options.common.config.option.ConfigOption;
import games.enchanted.enchanteds_sodium_options.common.config.option.IntOption;
import games.enchanted.enchanteds_sodium_options.common.config.option.integer.CollapseThresholdOption;
import net.caffeinemc.mods.sodium.api.config.option.Range;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigOptions {
    private static final List<ConfigOption<?>> OPTIONS = new ArrayList<>();

    // visual
    public static final ConfigOption<Boolean> ACCENT_BARS = registerOption(new BoolOption(
        true,
        true,
        "accent_bars"
    ));

    public static final ConfigOption<Boolean> SHOW_MOD_ICONS = registerOption(new BoolOption(
        true,
        true,
        "show_mod_icons"
    ));

    public static final ConfigOption<Boolean> COLOURED_HEADER_TEXT = registerOption(new BoolOption(
        true,
        true,
        "coloured_header_text"
    ));

    public static final ConfigOption<Boolean> COLOURED_CATEGORY_TEXT = registerOption(new BoolOption(
        true,
        true,
        "coloured_category_text"
    ));

    // behaviour
    public static final ConfigOption<Integer> COLLAPSE_THRESHOLD = registerOption(new CollapseThresholdOption(
        6,
        6,
        new Range(0, 48, 1),
        "collapse_threshold"
    ));

    public static final ConfigOption<Boolean> COLLAPSE_SODIUM_OPTIONS = registerOption(new BoolOption(
        false,
        false,
        "collapse_sodium_options"
    ));


    static {
        readConfig();
    }

    private static <T> ConfigOption<T> registerOption(ConfigOption<T> option) {
        OPTIONS.add(option);
        return option;
    }

    private static final String FILE_NAME = ModConstants.MOD_ID + ".json";

    private static File getConfigFile() {
        return PlatformHelper.getConfigPath().resolve(FILE_NAME).toFile();
    }

    public static void saveIfAnyDirtyOptions() {
        if(OPTIONS.stream().noneMatch(ConfigOption::isDirty)) return;
        for (ConfigOption<?> option : OPTIONS) {
            if(option.isDirty()) option.applyPendingValue();
        }
        saveConfig();
    }

    public static void saveConfig() {
        JsonObject root = new JsonObject();

        for (ConfigOption<?> option : OPTIONS) {
            root.add(option.getJsonKey(), option.toJson());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String encodedJson = gson.toJson(root);

        try (FileWriter writer = new FileWriter(getConfigFile())) {
            writer.write(encodedJson);
        } catch (IOException e) {
            Logging.error("Failed to write config file '{}', {}", FILE_NAME, e);
        }
    }

    public static void readConfig() {
        Gson gson = new Gson();
        JsonObject decodedConfig = new JsonObject();

        try {
            JsonReader jsonReader = gson.newJsonReader(new FileReader(getConfigFile()));
            jsonReader.setStrictness(Strictness.LENIENT);
            decodedConfig = JsonParser.parseReader(jsonReader).getAsJsonObject();
        } catch (JsonParseException e) {
            Logging.error("Failed to parse config file '{}', {}", FILE_NAME, e);
        } catch (FileNotFoundException e) {
            Logging.info("Config file '{}' not found", FILE_NAME);
            saveConfig();
        }

        for (ConfigOption<?> option : OPTIONS) {
            option.fromJson(decodedConfig);
        }
    }

    public static void resetAndSaveAllOptions() {
        for (ConfigOption<?> option : OPTIONS) {
            option.resetToDefault(true);
        }
        saveConfig();
    }

    public static void clearAllPendingValues() {
        for (ConfigOption<?> option : OPTIONS) {
            option.clearPendingValue();
        }
    }
}
