package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.PlayerMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandMaintenance implements CommandExecutor, TabExecutor {
	private final EasyCraft plugin = EasyCraft.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			this.sendHelp(sender);
		} else {
			if (args[0].equalsIgnoreCase("on")) {
				this.plugin.getConfig().set("maintenance", "on");
				this.plugin.saveConfig();

				sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MAINTENANCE_ENABLED, sender));

				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if (!p.isOp()) {
						p.kick(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MAINTENANCE_ENABLED_KICK, p));
					}
				}

				for (Player p : plugin.getServer().getOnlinePlayers()) {
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MAINTENANCE_ENABLED_BROADCAST, p, sender.getName()));
				}
			} else {
				if (!args[0].equalsIgnoreCase("off")) {
					this.sendHelp(sender);
					return true;
				}

				this.plugin.getConfig().set("maintenance", "off");
				this.plugin.saveConfig();

				sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MAINTENANCE_DISABLED, sender));

				for (Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MAINTENANCE_DISABLED_BROADCAST, p, sender.getName()));
				}
			}

		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> cmds = new ArrayList<>();
		if (command.getName().equalsIgnoreCase("maintenance")) {
			if (args.length == 1) {
				cmds.add("on");
				cmds.add("off");
			}
		}
		return cmds;
	}

	public void sendHelp(CommandSender sender) {
		sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MAINTENANCE_HELP, sender, this.plugin.getConfig().get("maintenance") + ""));
	}
}
