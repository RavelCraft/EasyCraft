package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TpaManager {
	private final EasyCraft plugin = EasyCraft.getInstance();
	private Map<Player, TpaRequest> tpa = new HashMap<>();

	public int createRequest(final Player sender, final Player target, boolean isTpaHere) {
		if (target == null || sender == null) {
			return 1;
		}

		if (this.tpa.containsKey(target)) {
			return 1;
		} else {
			this.tpa.put(target, new TpaRequest(sender, isTpaHere));

			String tpaText;
			if (!isTpaHere) {
				tpaText = ChatColor.GOLD + "to you" + ChatColor.RESET;
			} else {
				tpaText = ChatColor.GOLD + "you to them" + ChatColor.RESET;
			}

			long keepAlive = 30L;
			if (this.plugin.getConfig().contains("tpa-keep-alive")) {
				keepAlive = this.plugin.getConfig().getLong("tpa-keep-alive");
			} else {
				this.plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "You have not specified a tpa keep alive time. Set it with tpa-keep-alive in config.yml!");
			}

			target.sendMessage(StringUtils.componentToString(sender.displayName()) + " would like to teleport " + tpaText + "! You may accept using " + ChatColor.GOLD + "/tpaccept" + ChatColor.RESET + " or deny the request with " + ChatColor.GOLD + "/tpdeny" + ChatColor.RESET + ".");
			try {
				this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.timedOut(sender, target), keepAlive * 20L);
			} catch (Exception e) {
				EasyCraft.getLog().severe("Failed to start tpa timeout: " + e.getMessage());
				this.timedOut(sender, target);
			}
			return 0;
		}
	}

	public int tpaAccept(Player accept) {
		if (!this.tpa.containsKey(accept)) {
			return 1;
		} else {
			TpaRequest tpaRequest = this.tpa.get(accept);
			if (!tpaRequest.getSender().isOnline()) {
				return 2;
			} else {
				tpaRequest.getSender().sendMessage(StringUtils.componentToString(accept.displayName()) + " has accepted your tpa request!");
				if (!tpaRequest.isTpaHere()) {
					tpaRequest.getSender().teleport(accept);
				} else {
					accept.teleport(tpaRequest.getSender());
				}

				this.tpa.remove(accept);
				return 0;
			}
		}
	}

	public int tpaDeny(Player deny) {
		if (!this.tpa.containsKey(deny)) {
			return 1;
		} else {
			TpaRequest tpaRequest = this.tpa.get(deny);
			if (!tpaRequest.getSender().isOnline()) {
				return 2;
			} else {
				tpaRequest.getSender().sendMessage(ChatColor.RED + StringUtils.componentToString(deny.displayName()) + ChatColor.RED + " has denied your tpa request...");
				this.tpa.remove(deny);
				return 0;
			}
		}
	}

	public void timedOut(Player sender, Player target) {
		if (this.tpa.containsKey(target)) {
			if (sender.isOnline()) {
				sender.sendMessage(ChatColor.RED + "You teleport request for " + StringUtils.componentToString(target.displayName()) + ChatColor.RED + " has timed out!");
			}

			this.tpa.remove(target);
		}
	}

	private record TpaRequest(Player sender, boolean isTpaHere) {
		public Player getSender() {
			return sender;
		}
	}
}
