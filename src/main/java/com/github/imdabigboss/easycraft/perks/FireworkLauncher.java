package com.github.imdabigboss.easycraft.perks;

import com.github.imdabigboss.easycraft.EasyCraft;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireworkLauncher extends RavelPerk implements Listener {
    public static final String NAME = "fireworklauncher";
    public static final int CUSTOM_MODEL_DATA = 982645;

    private final Random random = new Random();
    private final EasyCraft plugin = EasyCraft.getInstance();

    public FireworkLauncher() {
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
        meta.displayName(Component.text(ChatColor.AQUA + "Firework Launcher"));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.GRAY + "Right-click to launch a"));
        lore.add(Component.text(ChatColor.GRAY + "firework."));
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

    private Color randomColor() {
        int red = (int) (Math.random() * 256);
        int green = (int) (Math.random() * 256);
        int blue = (int) (Math.random() * 256);
        return Color.fromRGB(red, green, blue);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (event.getItem() != null && event.getItem().getType() == Material.STICK && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasCustomModelData() && event.getItem().getItemMeta().getCustomModelData() == CUSTOM_MODEL_DATA) {
            Player player = event.getPlayer();
            Location target = player.getTargetBlock(5).getLocation().add(0, 1, 0);
            World world = target.getWorld();

            Location topPos = world.getHighestBlockAt(target.getBlockX(), target.getBlockZ(), HeightMap.MOTION_BLOCKING).getLocation();
            boolean sky = target.getBlockY() > topPos.getBlockY();

            if (!sky) {
                player.sendMessage(ChatColor.RED + "You can only launch fireworks outside!");
                return;
            }

            Firework firework = (Firework) world.spawnEntity(target, EntityType.FIREWORK);

            FireworkMeta meta = firework.getFireworkMeta();
            meta.setPower(this.random.nextInt(2) + 1);
            meta.addEffect(FireworkEffect.builder()
                    .with(FireworkEffect.Type.values()[this.random.nextInt(FireworkEffect.Type.values().length)])
                    .withTrail()
                    .withFlicker()
                    .withColor(randomColor())
                    .withFade(randomColor())
                    .build());

            firework.setFireworkMeta(meta);
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
