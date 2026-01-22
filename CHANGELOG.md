# 1.1.0

## New features

- Updated to support Sodium 0.8.3
- Changed tooltips in the video settings screen to ensure they don't overflow outside the screen
- Added tr_tr translations (thanks to Arcdashckr on GitHub!)
- Added a 'Shaderpacks' button in the top right of the video settings screen when Iris is installed

## Changes

- Option values now show in tooltips. Fixes issues where it was impossible to tell the value of an option if its text was too long
- Using the arrows keys on sliders now changes the value by 1 step instead of moving the handle by 1 pixel (like the vanilla options screen)
- Version numbers are now truncated in the video settings screen, hovering over it shows the full version
- The mod's version number now includes the target sodium version
    - For example `1.1.0+0.8.3-mc1.21.11` is ESO v1.1.0, for Sodium v0.8.3 and Minecraft 1.21.11 

## Fixes

- Fixed some mods appearing in the video settings screen despite having no options. This happened when a mod registered an option overlay but no other options with Sodiums config api