package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandConfUpdate implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage("You may only warn players of an update as console!!!");
			return true;
		}

		EasyCraft plugin = EasyCraft.getInstance();

		long stopTime = 30L;
		if (plugin.getConfig().contains("confupdate-stop")) {
			stopTime = plugin.getConfig().getLong("confupdate-stop");
		}

		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "save-all");

		String message = ChatColor.RED +
				"This server will have to reboot because of some configuration or plugin updates!\n" +
				"You may come back online in about 5 minutes, you will be sent to the lobby or disconnected.";

		String consoleMessage = ChatColor.RED +
				"-----------------------------------------------------\n" +
				"YOU WILL HAVE TO START THE SERVER ONCE IT IS STOPPED!\n" +
				"-----------------------------------------------------";

		plugin.getServer().broadcast(Component.text(message));
		sender.sendMessage(consoleMessage);

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			for (Player player:  plugin.getServer().getOnlinePlayers()) {
				player.kick(Component.text("We are sorry " + ChatColor.GOLD + player.getName() + ChatColor.RESET + " but this server is restarting because of some configuration or plugin updates!" + ChatColor.GREEN + " Please check back in about 5 minutes."));
			}
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "stop");
		}, stopTime * 20L);

		return true;
	}
}