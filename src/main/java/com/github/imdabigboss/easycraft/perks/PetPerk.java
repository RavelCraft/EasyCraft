package com.github.imdabigboss.easycraft.perks;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class PetPerk extends RavelPerk implements Listener {
    public static final String NAME = "pet";

    private final EasyCraft plugin = EasyCraft.getInstance();

    private final List<PetData> pets = new ArrayList<>();

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

    @EventHandler
    public void onPetTakeDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.RABBIT) {
            return;
        }

        for (PetData pet : this.pets) {
            if (pet.base().equals(event.getEntity().getUniqueId()) || pet.head().equals(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
                break;
            }
        }
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

        Rabbit pet = (Rabbit) player.getWorld().spawnEntity(location, EntityType.RABBIT);
        pet.setSilent(true);
        pet.setInvulnerable(true);
        pet.setLootTable(null);
        pet.setRemoveWhenFarAway(false);
        pet.setAdult();
        pet.setPersistent(true);
        pet.setInvisible(true);
        pet.setCollidable(false);
        pet.setRemoveWhenFarAway(false);

        ArmorStand head = (ArmorStand) player.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        head.setItem(EquipmentSlot.HEAD, new ItemStack(Material.PLAYER_HEAD));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            head.addEquipmentLock(slot, ArmorStand.LockType.REMOVING_OR_CHANGING);
            head.addEquipmentLock(slot, ArmorStand.LockType.ADDING_OR_CHANGING);
        }
        head.setSmall(true);
        head.setCanTick(false);
        head.setVisible(false);
        head.setInvulnerable(true);
        head.setPersistent(true);
        head.setRemoveWhenFarAway(false);

        pet.addPassenger(head);
        this.pets.add(new PetData(pet.getUniqueId(), head.getUniqueId(), player.getUniqueId()));
        this.plugin.getServer().getMobGoals().removeAllGoals(pet);
        this.plugin.getServer().getMobGoals().addGoal(pet, 1, new FollowPlayerGoal(this.plugin, this, pet, head, player));
    }

    private void removePet(PetData data) {
        Entity tmp = this.plugin.getServer().getEntity(data.head());
        if (tmp != null) {
            tmp.remove();
        }
        tmp = this.plugin.getServer().getEntity(data.base());
        if (tmp != null) {
            tmp.remove();
        }
    }

    private void removePet(Player player) {
        PetData data = null;
        for (PetData pet : this.pets) {
            if (pet.player().equals(player.getUniqueId())) {
                data = pet;
                break;
            }
        }

        if (data != null) {
            this.removePet(data);
            this.pets.remove(data);
        }
    }

    public void removeAllPets() {
        for (PetData data : this.pets) {
            this.removePet(data);
        }

        this.pets.clear();
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return false;
    }
    private static class FollowPlayerGoal implements Goal<Rabbit> {
        private final GoalKey<Rabbit> key;
        private final PetPerk perk;
        private final Mob mob;
        private final ArmorStand head;
        private final Player owner;

        public FollowPlayerGoal(Plugin plugin, PetPerk perk, Mob mob, ArmorStand head, Player owner) {
            this.key = GoalKey.of(Rabbit.class, new NamespacedKey(plugin, "follow_player"));
            this.perk = perk;
            this.mob = mob;
            this.head = head;
            this.owner = owner;
        }

        @Override
        public boolean shouldActivate() {
            return this.isOwnerTooFar();
        }

        @Override
        public boolean shouldStayActive() {
            return this.isOwnerTooFar();
        }

        @Override
        public void start() {
            this.head.setHeadPose(new EulerAngle(0, 0, 0));
        }

        @Override
        public void stop() {
            this.mob.getPathfinder().stopPathfinding();
            this.mob.setTarget(null);
        }

        @Override
        public void tick() {
            this.mob.setTarget(this.owner);
            if (this.isOwnerTooFar()) {
                this.mob.getPathfinder().moveTo(this.owner, 2.4D);
            } else {
                this.mob.getPathfinder().stopPathfinding();
            }

            this.head.setRotation(this.mob.getEyeLocation().getYaw(), 0);
        }

        @Override
        public @NotNull GoalKey<Rabbit> getKey() {
            return this.key;
        }

        @Override
        public @NotNull EnumSet<GoalType> getTypes() {
            return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
        }

        private boolean isOwnerTooFar() {
            if (!this.mob.getWorld().getName().equals(this.owner.getWorld().getName())) {
                this.perk.removePet(this.owner);
                this.perk.summonPet(this.owner);

                return false;
            }

            Location mobLocation = this.mob.getLocation();
            mobLocation.setY(this.owner.getLocation().getY());

            double distance = mobLocation.distance(this.owner.getLocation());
            if (distance > 16) {
                this.teleportToPlayer();

                return false;
            }

            return distance > 3;
        }

        public void teleportToPlayer() {
            Location location = this.owner.getLocation();
            int minHeight = location.getWorld().getMinHeight() + 1;
            if (location.getY() <= minHeight) {
                return;
            }

            while (!location.clone().subtract(0, 1, 0).getBlock().isSolid()) {
                if (location.getY() <= minHeight) {
                    return;
                }

                location.subtract(0, 1, 0);
            }

            this.mob.removePassenger(this.head);
            this.mob.teleport(location);
            this.head.teleport(this.mob.getLocation());
            this.mob.addPassenger(this.head);
        }
    }

    private record PetData(UUID base, UUID head, UUID player) {
    }
}

