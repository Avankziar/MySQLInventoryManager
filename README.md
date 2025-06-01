# Introduction
MIM is a plugin that synchronizes player-related data across servers.
When a player joins a server, their actions (interactions, etc.) are blocked until the plugin has synchronized their data.

# Dependencies
The plugin has the following dependencies on other plugins and environments:
* Spigot
* InterfaceHub
  * BoostYaml
  * MySQL

# Commands & Permissions
None ;)

# Synchronization
MIM synchronizes:
- Inventory
- MetaData:
    - GameMode
    - MaxHealth
    - Health
    - RemainingAir
    - FireTicks
    - FreezeTicks
    - Level
    - Exp
    - Exhaustion
    - FoodLevel
    - Saturation
- EnderChest
- PotionEffects

# Config
All further information can be found in MIM's config.yml.