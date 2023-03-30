package com.github.imdabigboss.easycraft.commands.tpa;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.TpaManager;
import com.github.imdabigboss.easycraft.utils.PlayerMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandTpa implements CommandExecutor, TabExecutor {
	private final EasyCraft plugin = EasyCraft.getInstance();
	private final TpaManager tpaUtils = EasyCraft.getTpaManager();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			if (args.length != 1) {
				this.sendHelp(sender);
				return true;
			} else if (args[0].equals(sender.getName())) {
				sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_TP_SELF, sender));
				return true;
			} else {
				Player target;
				try {
					target = this.plugin.getServer().getPlayer(args[0]);
					if (target == null || !target.isOnline()) {
						sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_OFFLINE, sender, args[0]));
						return true;
					}
				} catch (Exception e) {
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_OFFLINE, sender, args[0]));
					return true;
				}

				int out = tpaUtils.createRequest((Player) sender, target, false);
				if (out == 0) {
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_REQUEST_TPA, sender, args[0]));
				} else if (out == 1) {
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_ALREADY_HAS_REQ, sender));
				}

				return true;
			}
		} else {
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MUST_BE_PLAYER, sender));
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> cmds = new ArrayList<>();
		if (args.length == 1) {
			for (Player player : this.plugin.getServer().getOnlinePlayers()) {
				cmds.add(player.getName());
			}
		}
		return cmds;
	}

	public void sendHelp(CommandSender sender) {
		sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_TPA_TPA_HELP, sender));
	}
}
