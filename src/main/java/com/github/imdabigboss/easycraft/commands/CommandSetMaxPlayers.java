package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CommandSetMaxPlayers implements CommandExecutor, TabExecutor {
	private final EasyCraft plugin = EasyCraft.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			if (args.length != 1) {
				sender.sendMessage("Choose the amount of players that can join!");
				return false;
			} else {
				int playerCount;
				try {
					playerCount = Integer.parseInt(args[0]);
					this.changeSlots(playerCount);
				} catch (Exception e) {
					sender.sendMessage("An error occurred!");
					return false;
				}

				if (this.plugin.getServer().getMaxPlayers() != playerCount) {
					sender.sendMessage("An error occurred!");
				} else if (this.plugin.getServer().getMaxPlayers() == 0) {
					sender.sendMessage(ChatColor.RED + "WARNING! YOU SET THE MAX PLAYER COUNT TO 0!");
				} else {
					sender.sendMessage("Successfully set the max player count to: " + playerCount + "!");
				}

				return true;
			}
		} else {
			sender.sendMessage("You may only set the max player count as console!!!");
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> cmds = new ArrayList<>();
		if (command.getName().equalsIgnoreCase("setmaxplayers")) {
			cmds.add("<number>");
		}
		return cmds;
	}

	public void changeSlots(int slots) throws ReflectiveOperationException {
		Method serverGetHandle = this.plugin.getServer().getClass().getDeclaredMethod("getHandle");
		Object playerList = serverGetHandle.invoke(this.plugin.getServer());
		Field maxPlayersField = playerList.getClass().getSuperclass().getDeclaredField("maxPlayers");
		maxPlayersField.setAccessible(true);
		maxPlayersField.set(playerList, slots);
	}
}
