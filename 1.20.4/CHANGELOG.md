# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v20.4.0-1.20.4] - 2024-02-14
- Port to Minecraft 1.20.4
- Port to NeoForge
### Added
- Add biome tags for preventing mutant spawns: 
  - `mutantmonsters:without_mutant_creeper_spawns`
  - `mutantmonsters:without_mutant_enderman_spawns`
  - `mutantmonsters:without_mutant_skeleton_spawns`
  - `mutantmonsters:without_mutant_zombie_spawns`
### Changed
- Refactor all data files to use data generation
- Skeleton armor can now be repaired using bone blocks
### Fixed
- Fix some mutant attacks ignoring statues effects
