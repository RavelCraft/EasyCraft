package com.github.imdabigboss.easycraft.commands.tpa;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.TpaManager;
import org.bukkit.ChatColor;
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
				sender.sendMessage(ChatColor.RED + "You can not teleport to yourself!");
				return true;
			} else {
				Player target;
				try {
					target = this.plugin.getServer().getPlayer(args[0]);
					if (target == null || !target.isOnline()) {
						sender.sendMessage(ChatColor.RED + args[0] + " is not online!");
						return true;
					}
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + args[0] + " is not online!");
					return true;
				}

				int out = tpaUtils.createRequest((Player) sender, target, false);
				if (out == 0) {
					sender.sendMessage("You sent a request to " + args[0] + " so that you can teleport to them!");
				} else if (out == 1) {
					sender.sendMessage(ChatColor.RED + "This player already has a tpa request pending... Please wait!");
				}

				return true;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
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
		sender.sendMessage("The correct usage is:");
		sender.sendMessage("/tpa <player>");
	}
}
