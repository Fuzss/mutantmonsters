# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog].

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
