package com.github.imdabigboss.easycraft.commands.tpa;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.TpaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTpaccept implements CommandExecutor {
	private final TpaManager tpaUtils = EasyCraft.getTpaManager();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			if (args.length != 0) {
				this.sendHelp(sender);
			} else {
				int out = tpaUtils.tpaAccept(player);
				if (out == 0) {
					sender.sendMessage("Tpa request accepted!");
				} else if (out == 1) {
					sender.sendMessage(ChatColor.RED + "You do not have any pending tpa requests!");
				} else if (out == 2) {
					sender.sendMessage("That player is no longer online... sorry!");
				}

			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
		}
		return true;
	}

	public void sendHelp(CommandSender sender) {
		sender.sendMessage("The correct usage is:");
		sender.sendMessage("/tpaccept");
	}
}
