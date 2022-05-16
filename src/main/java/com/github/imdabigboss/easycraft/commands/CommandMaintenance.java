package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
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

	private static final String MAINTENANCE = "maintenance";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 1) {
			this.sendHelp(sender);
		} else {
			if (args[0].equalsIgnoreCase("on")) {
				this.plugin.getConfig().set(MAINTENANCE, "on");
				this.plugin.saveConfig();

				sender.sendMessage(ChatColor.AQUA + "Turned on maintenance mode!");
				this.kickPlayers(sender.getName());
			} else {
				if (!args[0].equalsIgnoreCase("off")) {
					this.sendHelp(sender);
					return true;
				}

				this.plugin.getConfig().set(MAINTENANCE, "off");
				this.plugin.saveConfig();

				sender.sendMessage(ChatColor.AQUA + "Turned off maintenance mode!");
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage(ChatColor.AQUA + sender.getName() + " has disabled maintenance mode!");
				}
			}

		}
		return true;
	}

	private void kickPlayers(String player) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (!p.isOp()) {
				p.kick(Component.text("We very sorry " + ChatColor.YELLOW + p.getName() + ChatColor.RESET + " but the server has been put under " + ChatColor.RED + "maintenance mode"));
			}
		}

		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.sendMessage(ChatColor.AQUA + player + " has enabled maintenance mode!");
		}

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
		sender.sendMessage("Maintenance mode is " + this.plugin.getConfig().get(MAINTENANCE));
		sender.sendMessage("You may set maintenance mode using 'on' or 'off'!");
	}
}
