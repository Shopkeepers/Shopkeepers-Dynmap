# Shopkeepers Dynmap Integration

This is a Bukkit plugin that shows shopkeepers as markers on the [Dynmap](https://www.spigotmc.org/resources/dynmap%C2%AE.274/).

## Prerequisites

- An up-to-date [Spigot](https://www.spigotmc.org/) or [Paper](https://papermc.io/) Minecraft server. Tested on: Spigot 1.21.11
- [Shopkeepers plugin](https://www.spigotmc.org/resources/shopkeepers.80756/). Tested on: 2.25.0
- [Dynmap plugin](https://www.spigotmc.org/resources/dynmap%C2%AE.274/). Tested on: 3.7-beta-11-1.21.11-spigot (jacob1 fork).

## Installation

- Drop the plugin jar into your Bukkit server's `plugins` folder.
- Restart your server.
- Adjust the `plugins/Shopkeepers-Dynmap/config.yml` file as needed.

## Commands

Base command: `/shopkeepers-dynmap`  
Aliases: `shopkeeper-dynmap`, `skdm`

- `/shopkeepers-dynmap help`: Shows the help page.  
  Permission: `shopkeepers-dynmap.help` (default: `true`)
- `/shopkeepers-dynmap reload`: Reloads the plugin and config.  
  Permission: `shopkeepers-dynmap.reload` (default: `op`)
- `/shopkeepers-dynmap update-icons`: Updates the Dynmap marker icons.  
  Permission: `shopkeepers-dynmap.reload` (default: `op`)  
  This deletes all existing marker icons with the `shopkeepers-` prefix (!) and then re-creates the current icons from the plugin jar.
