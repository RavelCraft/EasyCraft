package com.github.imdabigboss.easycraft.commands.home;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandDelHome implements CommandExecutor, TabExecutor {
	private final ConfigManager.YmlConfig homesYML = EasyCraft.getConfig("homes");

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		int maxHomes = 0;
		if (homesYML.getConfig().contains("maxHomes")) {
			maxHomes = homesYML.getConfig().getInt("maxHomes");
		}

		if (args.length != 1) {
			sender.sendMessage(ChatColor.AQUA + "You must enter a home number! Your max home count is: " + maxHomes);
		} else if (sender instanceof Player player) {
			String info = player.getUniqueId() + "." + args[0];
			if (!homesYML.getConfig().contains(info + ".World")) {
				sender.sendMessage("You have no home with that number!");
			} else {
				homesYML.getConfig().set(info, null);
				homesYML.saveConfig();
				sender.sendMessage(ChatColor.AQUA + "You have deleted your home!");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
		}
		return true;
	}

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
