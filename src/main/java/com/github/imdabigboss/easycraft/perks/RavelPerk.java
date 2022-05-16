package com.github.imdabigboss.easycraft.perks;

import org.bukkit.entity.Player;

public abstract class RavelPerk {
    private final String name;
    private final int level;
    private final boolean canBuyMultiple;

    public RavelPerk(String name, int level, boolean canBuyMultiple) {
        this.name = name;
        this.level = level;
        this.canBuyMultiple = canBuyMultiple;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean canBuyMultiple() {
        return this.canBuyMultiple;
    }

    public abstract boolean getPerk(Player player, String[] args);
}
