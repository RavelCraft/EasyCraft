package com.github.imdabigboss.easycraft.perks;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.perks.pet.FollowPlayerGoal;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PetPerk extends RavelPerk implements Listener {
    public static final String NAME = "pet";

    private final EasyCraft plugin = EasyCraft.getInstance();

    private final Map<UUID, Entity> pets = new HashMap<>();

    public PetPerk() {
        super(NAME, 3, false);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
        }

        this.summonPet(player);
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.summonPet(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        this.removePet(event.getPlayer());
    }

    private void summonPet(Player player) {
        if (!EasyCraft.getPerksManager().playerHasPerk(player.getUniqueId(), NAME)) {
            return;
        }

        Location location = player.getLocation();
        if (location.getY() <= location.getWorld().getMinHeight() + 1) {
            while (!location.clone().subtract(0, 1, 0).getBlock().isSolid()) {
                if (location.getY() <= location.getWorld().getMinHeight() + 1) {
                    location = player.getLocation();
                    break;
                }

                location.subtract(0, 1, 0);
            }
        }

        Zombie pet = (Zombie) player.getWorld().spawnEntity(location, EntityType.ZOMBIE);
        pet.customName(Component.text(player.getName() + "'s " + pet.getType().toString().toLowerCase(Locale.ROOT).replace('_', ' ')));

        pet.setSilent(true);
        pet.setInvulnerable(true);
        pet.setLootTable(null);
        pet.setArmsRaised(false);
        pet.setCanBreakDoors(false);
        pet.setShouldBurnInDay(false);
        pet.setRemoveWhenFarAway(false);
        pet.setAdult();
        pet.setPersistent(true);

        this.pets.put(player.getUniqueId(), pet);

        this.plugin.getServer().getMobGoals().removeAllGoals(pet);
        this.plugin.getServer().getMobGoals().addGoal(pet, 1, new FollowPlayerGoal(this.plugin, pet, player));
    }

    private void removePet(Player player) {
        Entity pet = this.pets.remove(player.getUniqueId());

        if (pet != null) {
            pet.remove();
        }
    }

    public void removeAllPets() {
        for (Entity pet : this.pets.values()) {
            pet.remove();
        }

        this.pets.clear();
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return false;
    }
}

