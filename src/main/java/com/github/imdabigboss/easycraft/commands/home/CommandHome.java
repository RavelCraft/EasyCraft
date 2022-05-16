package com.github.imdabigboss.easycraft.commands.home;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHome implements CommandExecutor, TabExecutor {
	private final EasyCraft plugin = EasyCraft.getInstance();
	private final ConfigManager.YmlConfig homesYML = EasyCraft.getConfig("homes");

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		int maxHomes = 0;
		if (homesYML.getConfig().contains("maxHomes")) {
			maxHomes = homesYML.getConfig().getInt("maxHomes");
		}

		if (args.length != 1) {
			sender.sendMessage(ChatColor.AQUA + "You must enter a home number! Your max home count is: " + maxHomes);
			return true;
		} else if (sender instanceof Player player) {
			String info = player.getUniqueId() + "." + args[0];
			if (!homesYML.getConfig().contains(info + ".World")) {
				player.sendMessage("You have no home with that number!\nYou must go /sethome <homeNumber>");
			} else {
				World world = this.plugin.getServer().getWorld(homesYML.getConfig().getString(info + ".World"));
				if (world == null) {
					player.sendMessage(ChatColor.RED + "An error occurred during teleportation!");
				} else {
					Location loc = player.getLocation();
					loc.setWorld(world);
					loc.setYaw((float) homesYML.getConfig().getDouble(info + ".Yaw", 0.0D));
					loc.setPitch((float) homesYML.getConfig().getDouble(info + ".Pitch", 0.0D));
					loc.setX(homesYML.getConfig().getDouble(info + ".X"));
					loc.setY(homesYML.getConfig().getDouble(info + ".Y"));
					loc.setZ(homesYML.getConfig().getDouble(info + ".Z"));
					player.teleport(loc);
					player.sendMessage(ChatColor.AQUA + "You have been teleported home!");
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> cmds = new ArrayList<>();
		if (sender instanceof Player player) {
			String uuid = player.getUniqueId().toString();
			if (args.length == 1) {
				int maxHomes = 0;
				if (homesYML.getConfig().contains("maxHomes")) {
					maxHomes = homesYML.getConfig().getInt("maxHomes");
				}
				if (maxHomes > 10) {
					maxHomes = 10;
				}

				for (int i = 1; i < maxHomes; i++) {
					if (homesYML.getConfig().contains(uuid + "." + i + ".World")) {
						cmds.add(Integer.toString(i));
					}
				}
			}
		}
		return cmds;
	}
}
