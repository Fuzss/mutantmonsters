# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

## [v8.0.7-1.20.1] - 2024-02-14
### Added
- Add a config option for the max distance of the ender soul hand's teleport ability, also allows for disabling the ability all together
### Fixed
- Fix crash when the mutant skeleton is firing arrows with the Bewitchment mod installed
- Fix unable to use chemical x on Fabric

## [v8.0.6-1.20.1] - 2023-12-03
### Changed
- Revert spawn category changes from last version for now

## [v8.0.5-1.20.1] - 2023-11-30
### Changed
- Mutants now use a custom mob category for spawning, this ensures only a single mutant can exist at a time
- The amount of entities spawned as a result of defeating a mutant (mainly skeleton parts and creeper eggs) is now capped to prevent issues with automatic mob farms
- Reworked mutant skeleton arrow physics
### Fixed
- Fix rendering crash with mutant enderman
- Fix rare crash when mutant zombie is attacking
- Fix thrown blocks being able to replace non-full blocks (like stairs and slabs) when landing

## [v8.0.4-1.20.1] - 2023-09-06
### Fixed
- Fixed client crash when the mutant enderman tries to drop loot

## [v8.0.3-1.20.1] - 2023-08-07
### Changed
- The mutant enderman now uses a separate loot table at `mutantmonsters:entities/mutant_enderman_continuous` for the continuous ender pearl drops while it is dying
- For items only dropped once at the end of the death sequence the vanilla loot table format `mutantmonsters:entities/mutant_enderman` is used which is empty by default, but can be used for custom death drops from a data pack
- Greatly reduced mutant spawn rates in soul sand valley and warped forest biomes, mutants are still more common in these biomes compared to others, but a lot less so now
### Fixed
- Fixed mutant zombie model hovering slightly above the ground

## [v8.0.2-1.20.1] - 2023-07-02
### Changed
- Mutant monsters will now destroy leaves in their path when chasing a target, similar to ravagers
### Fixed
- Fixed the Hulk Smash! advancement being obtained whenever a mutant zombie is killed
- Fixed mutant snow golems overriding waterlogged blocks with ice

## [v8.0.1-1.20.1] - 2023-07-01
### Added
- Added beautiful new textures in Minecraft's new art style provided by [tdstress](https://www.curseforge.com/members/tdstress)
### Changed
- Some adjustment to the creeper minion interaction screen
### Fixed
- Fixed a rare issue where rendering a teleporting mutant enderman could crash the game
- The ender soul hand held model no longer shows an enchantment glint as it was conflicting with the hand's texture

## [v8.0.0-1.20.1] - 2023-06-27
- Ported to Minecraft 1.20.1

[Keep a Changelog]: https://keepachangelog.com/en/1.0.0/
