package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.perks.EnderchestPerk;
import com.github.imdabigboss.easycraft.utils.PlayerMessage;
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
				sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_ENDERCHEST_OPEN, sender));
				player.openInventory(player.getEnderChest());
			} else {
				sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_ENDERCHEST_NO_PERK, sender));
			}
		} else {
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MUST_BE_PLAYER, sender));
		}
		return true;
	}
}
