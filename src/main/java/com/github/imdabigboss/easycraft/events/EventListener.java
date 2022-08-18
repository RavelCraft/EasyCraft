package com.github.imdabigboss.easycraft.events;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.PluginMessageManager;
import com.github.imdabigboss.easycraft.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class EventListener implements Listener {
	private final EasyCraft plugin = EasyCraft.getInstance();
	private final Map<UUID, ItemStack[]> shouldHavePerkItem = new HashMap<>();

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

		event.joinMessage(Component.text(ChatColor.YELLOW + player.getName() + " joined the server"));

		EasyCraft.getPluginMessageManager().readQueuedPluginMessages();
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
		Player player = event.getEntity();
		List<ItemStack> undroppableItems = new ArrayList<>();

		for (ItemStack item : event.getDrops()) {
			if (EasyCraft.getPerksManager().isUndroppable(item)) {
				undroppableItems.add(item);
			}
		}

		List<ItemStack> addToInventory = new ArrayList<>();

		for (ItemStack item : undroppableItems) {
			event.getDrops().remove(item);
			addToInventory.add(item.clone());
		}

		this.shouldHavePerkItem.remove(player.getUniqueId());
		this.shouldHavePerkItem.put(player.getUniqueId(), addToInventory.toArray(new ItemStack[0]));
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();

		if (this.shouldHavePerkItem.containsKey(player.getUniqueId())) {
			for (ItemStack item : this.shouldHavePerkItem.get(player.getUniqueId())) {
				player.getInventory().addItem(item);
			}
		}

		this.shouldHavePerkItem.remove(player.getUniqueId());
	}

	@EventHandler
	public void chatFormat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		event.getRecipients().clear(); //Don't cancel the event so that the server can log the message
		event.setFormat(p.getName() + ": " + event.getMessage());

		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
			player.sendMessage(ChatColor.WHITE + StringUtils.componentToString(p.displayName()) + ChatColor.WHITE + ": " + event.getMessage());
		}
	}
}
