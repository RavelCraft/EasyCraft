package com.github.imdabigboss.easycraft.commands.home;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.ConfigManager;
import com.github.imdabigboss.easycraft.utils.PlayerMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSetHome implements CommandExecutor, TabExecutor {
	private final ConfigManager.YmlConfig homesYML = EasyCraft.getConfig("homes");

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		int maxHomes = 0;
		if (homesYML.getConfig().contains("maxHomes")) {
			maxHomes = homesYML.getConfig().getInt("maxHomes");
		}

		if (args.length != 1) {
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_HOME_ENTER_HOME_NUMBER, sender, maxHomes + ""));
			return true;
		} else if (sender instanceof Player player) {
			try {
				int homeNumber = Integer.parseInt(args[0]);
				if (homeNumber > maxHomes || homeNumber <= 0) {
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_HOME_TOO_LARGE, sender, maxHomes + ""));
					return true;
				}
			} catch (Exception e) {
				sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_HOME_NAN, sender));
				return true;
			}

			String info = player.getUniqueId() + "." + args[0];
			homesYML.getConfig().set(info + ".World", player.getWorld().getName());
			Location loc = player.getLocation();
			homesYML.getConfig().set(info + ".X", loc.getX());
			homesYML.getConfig().set(info + ".Y", loc.getY());
			homesYML.getConfig().set(info + ".Z", loc.getZ());
			homesYML.getConfig().set(info + ".Pitch", loc.getPitch());
			homesYML.getConfig().set(info + ".Yaw", loc.getYaw());
			homesYML.saveConfig();
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_HOME_SET, sender));
		} else {
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MUST_BE_PLAYER, sender));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> cmds = new ArrayList<>();
		if (sender instanceof Player) {
			if (args.length == 1) {
				int maxHomes = 0;
				if (homesYML.getConfig().contains("maxHomes")) {
					maxHomes = homesYML.getConfig().getInt("maxHomes");
				}
				if (maxHomes > 10) {
					maxHomes = 10;
				}

				for (int i = 1; i < maxHomes; i++) {
					cmds.add(Integer.toString(i));
				}
			}
		}
		return cmds;
	}
}
