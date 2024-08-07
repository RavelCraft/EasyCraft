package com.github.imdabigboss.easycraft.events;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Logger;

public class EventListener implements Listener {
	private final EasyCraft plugin = EasyCraft.getInstance();
	private final Map<UUID, ItemStack[]> shouldHavePerkItem = new HashMap<>();
	private final List<Material> farmlandBlocks = List.of(Material.FARMLAND);

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		EasyCraft.getPluginMessageManager().initConnection();

		Player player = event.getPlayer();
		if (this.plugin.getConfig().get("maintenance").equals("on") && !player.hasPermission("easycraft.maintenance.bypass")) {
			player.kick(Component.text("We very sorry " + ChatColor.YELLOW + player.getName() + ChatColor.RESET + " but the server has been put under " + ChatColor.RED + "maintenance mode"));
		}

		player.sendMessage(ChatColor.GREEN + "You joined the " + EasyCraft.getServerName() + ChatColor.GREEN + " server");

		if (this.plugin.getServer().getOnlinePlayers().size() >= this.plugin.getServer().getMaxPlayers()) {
			this.plugin.getServer().broadcast(Component.text("There is now " + ChatColor.RED + this.plugin.getServer().getOnlinePlayers().size() + ChatColor.RESET + " players connected, maybe a few players could leave to free up space for other players wanting to join!"));
		}

		event.joinMessage(Component.text(ChatColor.YELLOW + player.getName() + " joined the server"));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		event.quitMessage(Component.text(ChatColor.YELLOW + player.getName() + " left the server"));
	}

	@EventHandler
	public void onClick(PlayerInteractEntityEvent event) {
		if (event.getHand() == EquipmentSlot.HAND && event.getRightClicked() instanceof ItemFrame entity) {
			if (event.getPlayer().isSneaking() && entity.getItem().getType() == Material.AIR) {
				entity.setVisible(!entity.isVisible());
			}
		}
	}

	@EventHandler
	public void onMobTrample(EntityInteractEvent event) {
		if (event.getEntity() instanceof Player) {
			return;
		}

		if (this.farmlandBlocks.contains(event.getBlock().getType())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerTrample(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL && this.farmlandBlocks.contains(event.getClickedBlock().getType())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType() == EntityType.SHULKER) {
			event.getDrops().clear();
			ItemStack shulkerStack = new ItemStack(Material.SHULKER_SHELL, 2);
			event.getEntity().getWorld().dropItemNaturally(entity.getLocation(), shulkerStack);
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		event.setCancelled(true);

		String message = ChatColor.WHITE + StringUtils.componentToString(event.getPlayer().displayName()) + ChatColor.WHITE + ": " + event.getMessage();
		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
			player.sendMessage(message);
		}

		Logger.getLogger("Minecraft").info("[CHAT]: " + ChatColor.stripColor(message));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().toLowerCase(Locale.ROOT).startsWith("/msg")) {
			event.setCancelled(true);

			String[] args = event.getMessage().split(" ");
			if (args.length < 3) {
				event.getPlayer().sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
				return;
			}

			Player recipient = plugin.getServer().getPlayer(event.getMessage().split(" ")[1]);
			if (recipient == null) {
				event.getPlayer().sendMessage(ChatColor.RED + "That player is not online!");
				return;
			}

			String message = event.getMessage().substring(event.getMessage().indexOf(args[2]));
			event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "To " + ChatColor.WHITE + recipient.getDisplayName() + ": " + message);
			recipient.sendMessage(ChatColor.LIGHT_PURPLE + "From " + ChatColor.WHITE + event.getPlayer().getDisplayName() + ": " + message);
		}
	}
}
