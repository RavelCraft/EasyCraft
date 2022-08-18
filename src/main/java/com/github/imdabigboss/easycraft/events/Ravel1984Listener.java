package com.github.imdabigboss.easycraft.events;

import com.github.imdabigboss.easycraft.managers.Ravel1984Manager;
import com.github.imdabigboss.easycraft.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;

public class Ravel1984Listener implements Listener {
    private final Ravel1984Manager manager;

    public Ravel1984Listener(Ravel1984Manager manager) {
        this.manager = manager;
    }

    //Connections

    @EventHandler
    public void playerJoined(PlayerJoinEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("connections", "Joined the server", event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerLeft(PlayerQuitEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("connections", "Left the server: " + event.getReason().name(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerKicked(PlayerKickEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("connections", "Kicked from the server: " + event.getCause().name(), event.getPlayer().getUniqueId());
    }

    //Chat

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("chat", event.getMessage(), event.getPlayer().getUniqueId());
    }

    //Command

    @EventHandler
    public void playerCommand(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("command", event.getMessage(), event.getPlayer().getUniqueId());
    }

    //Teleport

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("teleport", "From: " + StringUtils.locationToString(event.getFrom()) + "; To: " + StringUtils.locationToString(event.getTo()), event.getPlayer().getUniqueId());
    }

    //Death

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        if (event.getEntity().isOp()) {
            return;
        }

        manager.logData("death", "Cause: " + StringUtils.componentToString(event.deathMessage()) + "; At " + StringUtils.locationToString(event.getPlayer().getLocation()), event.getEntity().getUniqueId());
    }

    //Blocks

    @EventHandler
    public void playerBreakBlock(BlockBreakEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("block", "Break: " + event.getBlock().getType().name() + " at " + StringUtils.locationToString(event.getBlock().getLocation()), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerPlaceBlock(BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("block", "Place: " + event.getBlock().getType().name() + " at " + StringUtils.locationToString(event.getBlock().getLocation()), event.getPlayer().getUniqueId());
    }

    //Containers

    @EventHandler
    public void inventoryMoveItem(InventoryClickEvent event) {
        if (event.getWhoClicked().isOp()) {
            return;
        }
        if (event.getClickedInventory() == null) {
            return;
        }

        List<InventoryType> checkTypes = new ArrayList<>();
        checkTypes.add(InventoryType.CHEST);
        checkTypes.add(InventoryType.ENDER_CHEST);
        checkTypes.add(InventoryType.BARREL);
        checkTypes.add(InventoryType.SHULKER_BOX);
        checkTypes.add(InventoryType.HOPPER);

        if (checkTypes.contains(event.getClickedInventory().getType())) {
            if (event.getCursor().getType() == Material.AIR && event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            manager.logData("container", "Type: " + event.getAction().name() + "; Container: " + event.getInventory().getType().name() + "; Item 1: " + event.getCurrentItem().getType().name() + " x" + event.getCurrentItem().getAmount() + "; Item 2: " + event.getCursor().getType().name() + " x" + event.getCursor().getAmount() + "; Player location: " + StringUtils.locationToString(event.getWhoClicked().getLocation()), event.getWhoClicked().getUniqueId());
        }
    }

    //Buckets

    @EventHandler
    public void playerBucketFill(PlayerBucketFillEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("bucket", "Filled at " + StringUtils.locationToString(event.getBlockClicked().getLocation()) + " with " + event.getItemStack().getType().name(), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("bucket", "Emptied at " + StringUtils.locationToString(event.getBlockClicked().getLocation()) + " with " + event.getBucket().name(), event.getPlayer().getUniqueId());
    }

    //Items
    @EventHandler
    public void playerPickupItem(PlayerAttemptPickupItemEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("item", "Picked up " + event.getItem().getItemStack().getType().name() + " x" + event.getItem().getItemStack().getAmount() + " at " + StringUtils.locationToString(event.getItem().getLocation()), event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerThrowItem(PlayerDropItemEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        manager.logData("item", "Dropped " + event.getItemDrop().getItemStack().getType().name() + " x" + event.getItemDrop().getItemStack().getAmount() + " at " + StringUtils.locationToString(event.getItemDrop().getLocation()), event.getPlayer().getUniqueId());
    }
}
