package com.github.imdabigboss.easycraft.commands.tpa;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.TpaManager;
import com.github.imdabigboss.easycraft.utils.PlayerMessage;
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
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_REQ_ACCEPTED_SENDER, sender));
				} else if (out == 1) {
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_NO_REQ, sender));
				} else if (out == 2) {
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_PLAYER_NO_LONGER_ONLINE, sender));
				}
			}
		} else {
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MUST_BE_PLAYER, sender));
		}
		return true;
	}

	public void sendHelp(CommandSender sender) {
		sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_ACCEPT_HELP, sender));
	}
}
