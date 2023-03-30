package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.Lock;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Ravel1984Manager implements Listener {
    private final String logPath;
    private final Queue<LogData> logDataQueue = new LinkedList<>();
    private long lastLogTime = 0;
    private final Lock lock = new Lock();

    public Ravel1984Manager() {
        EasyCraft plugin = EasyCraft.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        if (plugin.getConfig().contains("enable1984-path")) {
            this.logPath = plugin.getConfig().getString("enable1984-path");
        } else {
            this.logPath = plugin.getDataFolder().getAbsolutePath();
        }

        plugin.getConfig().set("enable1984-path", this.logPath);
        plugin.saveConfig();
    }

    public void logData(String dataType, String data, UUID uuid) {
        new Thread(() -> {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            File logFile = new File(logPath + "/" + dateFormatter.format(ZonedDateTime.now()) + "/" + uuid.toString() + "/" + dataType + ".txt");
            String logData = "[" + timeFormatter.format(ZonedDateTime.now()) + "] " + data + "\n";

            lock.lock();
            logDataQueue.add(new LogData(logFile, logData));
            lock.unlock();

            lock.lock();
            boolean condition = logDataQueue.size() > 900 || System.currentTimeMillis() > lastLogTime + (60 * 1000);
            lock.unlock();
            if (condition) {
                this.flushCache();
            }
        }).start();
    }

    public void flushCache() {
        lock.lock();
        lastLogTime = System.currentTimeMillis();
        int size = logDataQueue.size();
        lock.unlock();

        Map<File, List<String>> logDataMap = new HashMap<>();

        for (int i = 0; i < size; i++) {
            lock.lock();
            LogData logData = logDataQueue.poll();
            lock.unlock();

            logDataMap.computeIfAbsent(logData.file, k -> new ArrayList<>()).add(logData.data);
        }

        for (Map.Entry<File, List<String>> entry : logDataMap.entrySet()) {
            File file = entry.getKey();
            List<String> data = entry.getValue();

            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                for (String line : data) {
                    writer.write(line);
                }
                writer.close();
            } catch (IOException e) {
                EasyCraft.getLog().severe("Could not write to log file! " + e.getMessage());
            }
        }
    }

    private static class LogData {
        public File file;
        public String data;

        public LogData(File file, String data) {
            this.file = file;
            this.data = data;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoined(PlayerJoinEvent event) {
        this.logData("connections", "Joined the server", event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerLeft(PlayerQuitEvent event) {
        this.logData("connections", "Left the server: " + event.getReason().name(), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerKicked(PlayerKickEvent event) {
        this.logData("connections", "Kicked from the server: " + event.getCause().name(), event.getPlayer().getUniqueId());
    }

    //Chat

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerChat(AsyncPlayerChatEvent event) {
        this.logData("chat", event.getMessage(), event.getPlayer().getUniqueId());
    }

    //Command

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerCommand(PlayerCommandPreprocessEvent event) {
        this.logData("command", event.getMessage(), event.getPlayer().getUniqueId());
    }

    //Teleport

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent event) {
        this.logData("teleport", "From: " + StringUtils.locationToString(event.getFrom()) + "; To: " + StringUtils.locationToString(event.getTo()), event.getPlayer().getUniqueId());
    }

    //Death

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerDeath(PlayerDeathEvent event) {
        this.logData("death", "Cause: " + StringUtils.componentToString(event.deathMessage()) + "; At " + StringUtils.locationToString(event.getPlayer().getLocation()), event.getEntity().getUniqueId());
    }

    //Blocks

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerBreakBlock(BlockBreakEvent event) {
        this.logData("block", "Break: " + event.getBlock().getType().name() + " at " + StringUtils.locationToString(event.getBlock().getLocation()), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerPlaceBlock(BlockPlaceEvent event) {
        this.logData("block", "Place: " + event.getBlock().getType().name() + " at " + StringUtils.locationToString(event.getBlock().getLocation()), event.getPlayer().getUniqueId());
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

            this.logData("container", "Type: " + event.getAction().name() + "; Container: " + event.getClickedInventory().getType().name() + "; Item 1: " + event.getCurrentItem().getType().name() + " x" + event.getCurrentItem().getAmount() + "; Item 2: " + event.getCursor().getType().name() + " x" + event.getCursor().getAmount() + "; " + locType + " location: " + StringUtils.locationToString(location), event.getWhoClicked().getUniqueId());
        }
    }

    //Buckets

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerBucketFill(PlayerBucketFillEvent event) {
        this.logData("bucket", "Filled at " + StringUtils.locationToString(event.getBlockClicked().getLocation()) + " with " + event.getItemStack().getType().name(), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerBucketEmpty(PlayerBucketEmptyEvent event) {
        this.logData("bucket", "Emptied at " + StringUtils.locationToString(event.getBlockClicked().getLocation()) + " with " + event.getBucket().name(), event.getPlayer().getUniqueId());
    }

    //Items
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerPickupItem(PlayerAttemptPickupItemEvent event) {
        this.logData("item", "Picked up " + event.getItem().getItemStack().getType().name() + " x" + event.getItem().getItemStack().getAmount() + " at " + StringUtils.locationToString(event.getItem().getLocation()), event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerThrowItem(PlayerDropItemEvent event) {
        this.logData("item", "Dropped " + event.getItemDrop().getItemStack().getType().name() + " x" + event.getItemDrop().getItemStack().getAmount() + " at " + StringUtils.locationToString(event.getItemDrop().getLocation()), event.getPlayer().getUniqueId());
    }
}
