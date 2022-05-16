package com.github.imdabigboss.easycraft.commands.tpa;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.TpaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTpdeny implements CommandExecutor {
	private final TpaManager tpaUtils = EasyCraft.getTpaManager();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			if (args.length != 0) {
				this.sendHelp(sender);
			} else {
				int out = tpaUtils.tpaDeny(player);
				if (out == 0) {
					sender.sendMessage("You denied the tpa request!");
				} else if (out == 1) {
					sender.sendMessage(ChatColor.RED + "You do not have any pending tpa requests!");
				} else if (out == 2) {
					sender.sendMessage("You denied the tpa request, but the player was no longer online anyway.");
				}

			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
		}
		return true;
	}

	public void sendHelp(CommandSender sender) {
		sender.sendMessage("The correct usage is:");
		sender.sendMessage("/tpdeny");
	}
}
