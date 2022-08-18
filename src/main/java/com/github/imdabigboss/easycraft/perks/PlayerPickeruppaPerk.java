package com.github.imdabigboss.easycraft.perks;

import com.github.imdabigboss.easycraft.EasyCraft;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerPickeruppaPerk extends RavelPerk implements Listener {
    public static final String NAME = "playerpickeruppa";
    public static final int CUSTOM_MODEL_DATA = 829052;

    private final EasyCraft plugin = EasyCraft.getInstance();

    public PlayerPickeruppaPerk() {
        super(NAME, 1, false);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
        }

        ItemStack item = new ItemStack(Material.STICK);

        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(ChatColor.AQUA + "Player Pickeruppa"));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.GRAY + "Right-click a player to"));
        lore.add(Component.text(ChatColor.GRAY + "be picked up."));
        lore.add(Component.text(""));
        lore.add(Component.text(ChatColor.GRAY + "But sprint to pick the"));
        lore.add(Component.text(ChatColor.GRAY + "player up."));
        meta.lore(lore);

        meta.setCustomModelData(CUSTOM_MODEL_DATA);
        item.setItemMeta(meta);

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You have no space in your inventory!");
            return false;
        } else {
            player.getInventory().addItem(item);
        }

        return true;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        Player rider;
        Player ridden;

        if (event.getRightClicked() instanceof Player) {
            ridden = (Player) event.getRightClicked();
            rider = player;
        } else {
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (player.isSprinting()) {
            rider = ridden;
            ridden = player;
        }

        if (itemStack.getType() == Material.STICK && itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData() && itemStack.getItemMeta().getCustomModelData() == CUSTOM_MODEL_DATA) {
            if (ridden.getPassengers().size() == 0) {
                ridden.addPassenger(rider);
            } else {
                player.sendMessage(ChatColor.RED + "That player's head is already occupied!");
            }
        }
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return item.getType() == Material.STICK &&
                item.hasItemMeta() &&
                item.getItemMeta().hasCustomModelData() &&
                item.getItemMeta().getCustomModelData() == CUSTOM_MODEL_DATA;
    }
}
