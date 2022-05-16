package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.PerksManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandPerks implements CommandExecutor, TabExecutor {
	private final PerksManager perksManager = EasyCraft.getPerksManager();
	private final EasyCraft plugin = EasyCraft.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			this.sendHelp(sender);
			return true;
		}

		if (args[0].equalsIgnoreCase("list")) {
			if (args.length != 1) {
				this.sendHelp(sender);
				return true;
			}

			StringBuilder sb = new StringBuilder("Perks:\n");
			for (String perk : this.perksManager.listPerksAndLevels()) {
				sb.append("\n - ").append(perk);
			}

			sender.sendMessage(sb.toString());
		} else if (args[0].equalsIgnoreCase("get")) {
			if (args.length < 2) {
				this.sendHelp(sender);
				return true;
			}

			if (sender instanceof Player player) {
				boolean out = this.perksManager.playerGetPerk(player, Arrays.copyOfRange(args, 1, args.length));
				if (!out) {
					this.sendHelp(sender);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
			}
		} else if (args[0].equalsIgnoreCase("admin")) {
			if (!sender.isOp()) {
				this.sendHelp(sender);
				return true;
			}
			if (args.length < 2) {
				this.sendAdminHelp(sender);
				return true;
			}

			if (args[1].equalsIgnoreCase("get")) {
				if (args.length != 3) {
					this.sendAdminHelp(sender);
					return true;
				}

				UUID uuid = EasyCraft.getUUUIDManager().playerNameToUUID(args[2]);
				if (uuid != null) {
					StringBuilder sb = new StringBuilder();
					sb.append(ChatColor.AQUA);
					sb.append(args[2]).append(" is level ").append(this.perksManager.getPlayerLevel(uuid));

					sb.append(" and has the following perks: ");
					for (String perk : this.perksManager.getPlayerPerks(uuid)) {
						sb.append("\n - ").append(perk);
					}

					sender.sendMessage(sb.toString());
				} else {
					sender.sendMessage(ChatColor.RED + "That player does not exist!");
				}
			} else if (args[1].equalsIgnoreCase("set")) {
				if (args.length != 4) {
					this.sendAdminHelp(sender);
					return true;
				}

				UUID uuid = EasyCraft.getUUUIDManager().playerNameToUUID(args[2]);
				if (uuid != null) {
					int level;
					try {
						level = Integer.parseInt(args[3]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "That is not a valid number!");
						return true;
					}
					if (level == -1) {
						sender.sendMessage(ChatColor.YELLOW + "Be careful! You set the perk level to -1! This means that they will have unlimited perks!");
					}

					this.perksManager.setPlayerLevel(uuid, level);
					sender.sendMessage(ChatColor.AQUA + "Successfully set " + args[2] + "'s perk level to " + level);
				} else {
					sender.sendMessage(ChatColor.RED + "That player does not exist!");
				}
			}
		}

		return true;
	}

	public void sendHelp(CommandSender sender) {
		sender.sendMessage("Here is the correct usage:");
		sender.sendMessage("- /perks list");
		sender.sendMessage("- /perks get <perk name>");
		if (sender.isOp()) {
			this.sendAdminHelp(sender);
		}
	}

	public void sendAdminHelp(CommandSender sender) {
		sender.sendMessage("Here is the correct admin usage:");
		sender.sendMessage("- /perks admin set <player> <level>");
		sender.sendMessage("- /perks admin get <player>");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		ArrayList<String> cmds = new ArrayList<>();
		if (command.getName().equalsIgnoreCase("perks")) {
			if (args.length == 1) {
				cmds.add("list");
				cmds.add("get");
				if (sender.isOp()) {
					cmds.add("admin");
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("get")) {
					cmds.addAll(this.perksManager.listPerksString());
				} else if (args[0].equalsIgnoreCase("admin")) {
					if (sender.isOp()) {
						cmds.add("get");
						cmds.add("set");
					}
				}
			} else if (args.length == 3) {
				if (sender.isOp()) {
					if (args[1].equalsIgnoreCase("get") || args[1].equalsIgnoreCase("set")) {
						for (Player player : plugin.getServer().getOnlinePlayers()) {
							cmds.add(player.getName());
						}
					}
				}
			}
		}
		return cmds;
	}
}
