package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.perks.EnderchestPerk;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEnderchest implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			if (EasyCraft.getPerksManager().playerHasPerk(player.getUniqueId(), EnderchestPerk.NAME)) {
				player.sendMessage(ChatColor.AQUA + "Opening your enderchest");
				player.openInventory(player.getEnderChest());
			} else {
				player.sendMessage(ChatColor.RED + "You did not purchase this command!");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
		}
		return true;
	}
}
