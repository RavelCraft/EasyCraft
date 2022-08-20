package com.github.imdabigboss.easycraft.perks.pet;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class FollowPlayerGoal implements Goal<Zombie> {
    private final GoalKey<Zombie> key;
    private final Mob mob;
    private final Player owner;

    public FollowPlayerGoal(Plugin plugin, Mob mob, Player owner) {
        this.key = GoalKey.of(Zombie.class, new NamespacedKey(plugin, "follow_player"));
        this.mob = mob;
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
    }

    @Override
    public void stop() {
        mob.getPathfinder().stopPathfinding();
        mob.setTarget(null);
    }

    @Override
    public void tick() {
        this.mob.setTarget(this.owner);
        if (this.isOwnerTooFar()) {
            this.mob.getPathfinder().moveTo(this.owner, 1.2D);
        } else {
            this.mob.getPathfinder().stopPathfinding();
        }
    }

    @Override
    public @NotNull GoalKey<Zombie> getKey() {
        return this.key;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK);
    }

    private boolean isOwnerTooFar() {
        if (this.mob.getWorld().getUID() != this.owner.getWorld().getUID()) {
            this.teleportToPlayer();

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

        this.mob.teleport(location);
    }
}