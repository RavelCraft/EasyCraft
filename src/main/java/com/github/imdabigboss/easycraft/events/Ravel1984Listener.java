package com.github.imdabigboss.easycraft.events;

import com.github.imdabigboss.easycraft.managers.Ravel1984Manager;
import com.github.imdabigboss.easycraft.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class Ravel1984Listener implements Listener {
    private final Ravel1984Manager manager;

    public Ravel1984Listener(Ravel1984Manager manager) {
        this.manager = manager;
    }

    //Connections

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoined(PlayerJoinEvent event) {
        manager.logData("connections", "Joined the server", event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerLeft(PlayerQuitEvent event) {
        manager.logData("connections", "Left the server: " + event.getReason().name(), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerKicked(PlayerKickEvent event) {
        manager.logData("connections", "Kicked from the server: " + event.getCause().name(), event.getPlayer().getUniqueId());
    }

    //Chat

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerChat(AsyncPlayerChatEvent event) {
        manager.logData("chat", event.getMessage(), event.getPlayer().getUniqueId());
    }

    //Command

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerCommand(PlayerCommandPreprocessEvent event) {
        manager.logData("command", event.getMessage(), event.getPlayer().getUniqueId());
    }

    //Teleport

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent event) {
        manager.logData("teleport", "From: " + StringUtils.locationToString(event.getFrom()) + "; To: " + StringUtils.locationToString(event.getTo()), event.getPlayer().getUniqueId());
    }

    //Death

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent event) {
        manager.logData("death", "Cause: " + StringUtils.componentToString(event.deathMessage()) + "; At " + StringUtils.locationToString(event.getPlayer().getLocation()), event.getEntity().getUniqueId());
    }

    //Blocks

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerBreakBlock(BlockBreakEvent event) {
        manager.logData("block", "Break: " + event.getBlock().getType().name() + " at " + StringUtils.locationToString(event.getBlock().getLocation()), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerPlaceBlock(BlockPlaceEvent event) {
        manager.logData("block", "Place: " + event.getBlock().getType().name() + " at " + StringUtils.locationToString(event.getBlock().getLocation()), event.getPlayer().getUniqueId());
    }

    //Containers

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryMoveItem(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        List<InventoryType> checkTypes = new ArrayList<>();
        checkTypes.add(InventoryType.CHEST);
        checkTypes.add(InventoryType.ENDER_CHEST);
        checkTypes.add(InventoryType.BARREL);
        checkTypes.add(InventoryType.SHULKER_BOX);
        checkTypes.add(InventoryType.HOPPER);

        if (checkTypes.contains(event.getInventory().getType())) {
            if (event.getCursor().getType() == Material.AIR && event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            Location location;
            String locType;

            InventoryHolder holder = event.getInventory().getHolder();
            if (holder instanceof BlockInventoryHolder blockHolder) {
                location = blockHolder.getBlock().getLocation();
                locType = "Block";
            } else {
                location = event.getWhoClicked().getLocation();
                locType = "Player";
            }

            manager.logData("container", "Type: " + event.getAction().name() + "; Container: " + event.getClickedInventory().getType().name() + "; Item 1: " + event.getCurrentItem().getType().name() + " x" + event.getCurrentItem().getAmount() + "; Item 2: " + event.getCursor().getType().name() + " x" + event.getCursor().getAmount() + "; " + locType + " location: " + StringUtils.locationToString(location), event.getWhoClicked().getUniqueId());
        }
    }

    //Buckets

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerBucketFill(PlayerBucketFillEvent event) {
        manager.logData("bucket", "Filled at " + StringUtils.locationToString(event.getBlockClicked().getLocation()) + " with " + event.getItemStack().getType().name(), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerBucketEmpty(PlayerBucketEmptyEvent event) {
        manager.logData("bucket", "Emptied at " + StringUtils.locationToString(event.getBlockClicked().getLocation()) + " with " + event.getBucket().name(), event.getPlayer().getUniqueId());
    }

    //Items
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerPickupItem(PlayerAttemptPickupItemEvent event) {
        manager.logData("item", "Picked up " + event.getItem().getItemStack().getType().name() + " x" + event.getItem().getItemStack().getAmount() + " at " + StringUtils.locationToString(event.getItem().getLocation()), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerThrowItem(PlayerDropItemEvent event) {
        manager.logData("item", "Dropped " + event.getItemDrop().getItemStack().getType().name() + " x" + event.getItemDrop().getItemStack().getAmount() + " at " + StringUtils.locationToString(event.getItemDrop().getLocation()), event.getPlayer().getUniqueId());
    }
}
