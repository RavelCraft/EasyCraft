package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSuicide implements CommandExecutor {
	private final EasyCraft plugin = EasyCraft.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "kill \"" + sender.getName() + "\"");
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
		}

		return true;
	}
}
