package com.github.imdabigboss.easycraft.perks;

import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class ArrowTrailPerk extends RavelPerk implements Listener {
    public static final String NAME = "arrowtrail";

    private final EasyCraft plugin = EasyCraft.getInstance();
    private final Particle.DustOptions trailDustOptions = new Particle.DustOptions(Color.fromRGB(0, 255, 0), 2);
    private final Map<Projectile, BukkitTask> arrows = new HashMap<>();

    public ArrowTrailPerk() {
        super(NAME, 1, false);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
        }

        return true;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!event.getEntityType().equals(EntityType.ARROW)) {
            return;
        }

        Projectile entity = event.getEntity();

        if (entity.getShooter() instanceof Player player) {
            if (!EasyCraft.getPerksManager().playerHasPerk(player.getUniqueId(), NAME)) {
                return;
            }

            this.arrows.put(entity, new BukkitRunnable() {
                @Override
                public void run() {
                    World world = entity.getWorld();
                    Location location = entity.getLocation();
                    world.spawnParticle(Particle.REDSTONE, location, 2, trailDustOptions);
                    world.playSound(location, Sound.ENTITY_ITEM_PICKUP, 1, 1);
                }
            }.runTaskTimer(this.plugin, 0L, 1L));
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();

        if (entity.getShooter() instanceof Player) {
            BukkitTask task = this.arrows.get(entity);
            if (task != null) {
                task.cancel();
                this.arrows.remove(entity);
            }
        }
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return false;
    }
}
