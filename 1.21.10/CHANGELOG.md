# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v21.10.5-1.21.10] - 2025-12-26

### Changed

- Creeper minions on your shoulders can no longer be seen at all to fully bypass the
  `Received attachment change for unknown target!` issue on Fabric

## [v21.10.4-1.21.10] - 2025-12-25

### Changed

- Creeper minions carried on your shoulders can no longer be seen by other players in multiplayer as a workaround for
  the `Received attachment change for unknown target!` issue on Fabric (NeoForge remains unchanged)

## [v21.10.3-1.21.10] - 2025-12-07

### Changed

- Mutant endermen are no longer permitted to spawn on the main island in the End

### Fixed

- Fix mutant endermen being unable to spawn on the surface in the End dimension
- Fix mutant endermen becoming stuck when dying after being reloaded
- Fix crash caused by high spawn weights set in the common config file

## [v21.10.2-1.21.10] - 2025-11-17

### Fixed

- Fix a desync issue where a mutant zombie would vanish on the client but still be present on the server when it is
  killed while already on fire like with a flame enchanted bow
- Fix mutant creeper attacks causing `NaN` delta movement when both the creeper and the target are at the exact same
  position in the world
- Fix mutant zombies missing from the `minecraft:zombies` entity tag

## [v21.10.1-1.21.10] - 2025-10-20

### Fixed

- Fix `java.lang.ArithmeticException` when setting mutant spawn weight to zero

## [v21.10.0-1.21.10] - 2025-10-09

### Changed

- Update to Minecraft 1.21.0
