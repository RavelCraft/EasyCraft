package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.utils.PlayerMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class CommandHeadItem implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			PlayerInventory inv = player.getInventory();

			ItemStack handItem = inv.getItemInMainHand();
			ItemStack headItem = inv.getHelmet();

			inv.setHelmet(handItem);
			inv.setItemInMainHand(headItem);
		} else {
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MUST_BE_PLAYER, sender));
		}
		return true;
	}
}
