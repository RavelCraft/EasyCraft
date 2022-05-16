package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSpawn implements CommandExecutor, TabExecutor {
	private final EasyCraft plugin = EasyCraft.getInstance();

	private static final String SPAWN_LOC = "spawnLoc";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("set")) {
					if (!player.isOp()) {
						sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
						return true;
					}

					plugin.getConfig().set(SPAWN_LOC, player.getLocation());
					plugin.saveConfig();
					sender.sendMessage(ChatColor.AQUA + "Spawn location set!");
				}
			} else {
				Location spawnPoint;
				if (plugin.getConfig().contains(SPAWN_LOC)) {
					spawnPoint = plugin.getConfig().getLocation(SPAWN_LOC);
				} else {
					spawnPoint = this.plugin.getServer().getWorld("world").getSpawnLocation();
				}

				if (spawnPoint != null) {
					player.teleport(spawnPoint);
					sender.sendMessage("You have been teleported to spawn!");
				} else {
					sender.sendMessage("Unable to find spawn location!");
				}
			}
		} else {
			sender.sendMessage("You need to be a player to use this command!");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> cmds = new ArrayList<>();
		cmds.add("set");
		return cmds;
	}
}
