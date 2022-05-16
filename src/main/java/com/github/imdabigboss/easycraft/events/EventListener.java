package com.github.imdabigboss.easycraft.events;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EventListener implements Listener {
	private final EasyCraft plugin = EasyCraft.getInstance();
	private Map<String, ItemStack> shouldHaveMagicStick = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (this.plugin.getConfig().get("maintenance").equals("on") && !player.hasPermission("easycraft.maintenance.bypass")) {
			player.kick(Component.text("We very sorry " + ChatColor.YELLOW + player.getName() + ChatColor.RESET + " but the server has been put under " + ChatColor.RED + "maintenance mode"));
		}

		player.sendMessage(ChatColor.GREEN + "You joined the " + EasyCraft.getServerName() + ChatColor.GREEN + " server");

		if (this.plugin.getServer().getOnlinePlayers().size() >= this.plugin.getServer().getMaxPlayers()) {
			this.plugin.getServer().broadcast(Component.text("There is now " + ChatColor.RED + this.plugin.getServer().getOnlinePlayers().size() + ChatColor.RESET + " players connected, maybe a few players could leave to free up space for other players wanting to join!"));
		}

		EasyCraft.getPluginMessageManager().sendCmd(player, "playerjoined", player.getUniqueId().toString());
		event.joinMessage(Component.text(ChatColor.YELLOW + player.getName() + " joined the server"));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		event.quitMessage(Component.text(ChatColor.YELLOW + player.getName() + " left the server"));
	}

	@EventHandler
	public void onClick(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) {
			return;
		}

		if (event.getRightClicked() instanceof ItemFrame entity) {
			if (event.getPlayer().isSneaking() && entity.getItem().getType() == Material.AIR) {
				entity.setVisible(!entity.isVisible());
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		ItemStack stick = null;
		for (ItemStack item : event.getDrops()) {
			if (item.getType() == Material.STICK && item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == 502698) {
				stick = item.clone();
			}
		}

		Player player = event.getEntity();
		if (stick != null) {
			event.getDrops().remove(stick);
			shouldHaveMagicStick.put(player.getName(), stick);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		String playerName = event.getPlayer().getName();
		if (shouldHaveMagicStick.containsKey(playerName)) {
			event.getPlayer().getInventory().addItem(shouldHaveMagicStick.get(playerName));
			shouldHaveMagicStick.remove(playerName);
		}
	}

	@EventHandler
	public void chatFormat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		event.setFormat(ChatColor.WHITE + StringUtils.componentToString(p.displayName()) + ChatColor.WHITE + ": " + event.getMessage());
	}
}
