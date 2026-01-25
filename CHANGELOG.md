# 1.1.0

## New features

- Updated to support Sodium 0.8.3
- Changed tooltips in the video settings screen to ensure they don't overflow outside the screen
- Added tr_tr translations (thanks to Arcdashckr on GitHub!)
- Added a 'Shaderpacks' button in the top right of the video settings screen when Iris is installed

## Changes

- The mod's version number now includes the target Sodium version
    - For example `1.1.0+0.8.3-mc1.21.11` is ESO v1.1.0, for Sodium v0.8.3 and Minecraft 1.21.11
- Option values are now displayed in tooltips
    - Addresses some issues where it was difficult to tell what the value of an option was if its text was too long
- Using the arrows keys on sliders now changes the value by 1 step instead of moving the handle by 1 pixel to match the behavior of vanillas video settings screen
- Version numbers are now truncated in the video settings screen, hovering over it shows the full version

## Fixes

- Fixed some mods appearing in the video settings screen despite having no options. This happened when a mod registered an option overlay but no other options with Sodiums config api
- The 'Back' button can now always be pressed in sub-menus in the video settings screen

## 1.1.1

- Allow the mod to load on Sodium 0.8.4