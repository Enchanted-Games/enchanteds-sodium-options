# Enchanted's Mod Template

Multi-version, multi-loader mod template for Minecraft using Stonecutter, Fabric Loom, and ModDevGradle.

This template is set up for Minecraft 1.21.11, 26.1, and 26.2 (fabric & neoforge) by default.

This template was primarily made with my specific needs in mind, however you are free to expand upon and use this as a base for your own mods

## Setup

1. Find and replace all instances of `enchanteds_sodium_options` with your mod id. Do the same for `Enchanted's Sodium Options` with your mod name
2. Update `mod.group` in gradle.properties, then rename `src/main/java/games/enchanted/enchanteds_sodium_options/` to `src/main/java/{group}/{mod id}`
3. Rename `src/main/resources/enchanteds_sodium_options.mixins.json` to your mod id
4. Make sure the project and gradle is configured to use Java 25 your IDE
5. Let gradle sync and you should be good to go!

## More info
- Change Minecraft versions in settings.gradle.kts
   - By default there are buildscripts for Fabric (remap, >=1.14), Fabric (>=26.1), and NeoForge (>1.20.2), if you need other versions you may make your own buildscripts for them
- Use the stonecutter gradle tasks to switch between versions, run configurations should be generated automatically
- Set up to handle ClassTweakers and AccessTransformers automatically
   - for fabric place them under `src/main/resources/{mod id}.classtweaker`
   - for neoforge place them under `src/main/resources/META-INF/accesstransformer.cfg`
   - these should be placed under `versions/<version>`

## Add or remove versions
1. Open `settings.gradle.kts`
2. In the stonecutter block call `ver(<mc version>, <loaders>)`
   - supported loaders by default are `fabric`, `fabric_remap`, and `neoforge`
   - to add new loaders you can simply make a buildscript, for example, `build.forge.gradle.kts` and pass `forge` as a loader
3. Edit versioned properties in `stonecutter.properties.toml`

## Publishing (optional)
Publishing is disabled by default, to enable it:
1. Uncomment `publish.modrinth` and/or `publish.curseforge` in gradle.properties and fill in your modrinth/curseforge project ids
2. Copy and paste .env.template as .env, and enter in your modrinth and/or curseforge api keys. **IMPORTANT: Keep this file private! Do not share it anywhere**
    - Modrinth: generate a [PAT](https://modrinth.com/settings/pats) with the Create, Read, and Write Versions scopes
    - Curseforge: generate an [Api Token](https://legacy.curseforge.com/account/api-tokens)
3. Set the environment accordingly near the bottom of each build script, by default it is set up for a client side only mod
4. Optionally set additional minecraft versions in versioned properties with `publish.additionalVersions`, separated by comma 

Once set up, you can publish by:
1. Run `./gradlew publishMods` and check the information is correct
2. Set `publish.dry_run` to false in gradle.properties, then run the above command again to publish
