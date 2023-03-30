package com.github.imdabigboss.easycraft.commands.home;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.ConfigManager;
import com.github.imdabigboss.easycraft.utils.PlayerMessage;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandHome implements CommandExecutor, TabExecutor {
	private final EasyCraft plugin = EasyCraft.getInstance();
	private final ConfigManager.YmlConfig homesYML = EasyCraft.getConfig("homes");

	public CommandHome() {
		if (!homesYML.getConfig().contains("maxHomes")) {
			homesYML.getConfig().set("maxHomes", 2);
			homesYML.saveConfig();
		}
	}

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
			String info = player.getUniqueId() + "." + args[0];
			if (!homesYML.getConfig().contains(info + ".World")) {
				sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_HOME_NO_HOME_NUMBER, sender));
			} else {
				World world = this.plugin.getServer().getWorld(homesYML.getConfig().getString(info + ".World"));
				if (world == null) {
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_HOME_TELEPORT_ERROR, sender));
				} else {
					Location loc = player.getLocation();
					loc.setWorld(world);
					loc.setYaw((float) homesYML.getConfig().getDouble(info + ".Yaw", 0.0D));
					loc.setPitch((float) homesYML.getConfig().getDouble(info + ".Pitch", 0.0D));
					loc.setX(homesYML.getConfig().getDouble(info + ".X"));
					loc.setY(homesYML.getConfig().getDouble(info + ".Y"));
					loc.setZ(homesYML.getConfig().getDouble(info + ".Z"));
					player.teleport(loc);
					sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_HOME_TELEPORTED, sender));
				}
			}
		} else {
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MUST_BE_PLAYER, sender));
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> cmds = new ArrayList<>();
		if (sender instanceof Player player) {
			String uuid = player.getUniqueId().toString();
			if (args.length == 1) {
				int maxHomes = 0;
				if (homesYML.getConfig().contains("maxHomes")) {
					maxHomes = homesYML.getConfig().getInt("maxHomes");
				}
				if (maxHomes > 10) {
					maxHomes = 10;
				}

				for (int i = 1; i < maxHomes; i++) {
					if (homesYML.getConfig().contains(uuid + "." + i + ".World")) {
						cmds.add(Integer.toString(i));
					}
				}
			}
		}
		return cmds;
	}
}
