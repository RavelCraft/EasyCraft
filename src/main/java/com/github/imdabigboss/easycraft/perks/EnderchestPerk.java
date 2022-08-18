package com.github.imdabigboss.easycraft.perks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnderchestPerk extends RavelPerk {
    public static final String NAME = "enderchest";

    public EnderchestPerk() {
        super(NAME, 2, false);
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
        }

        player.sendMessage(ChatColor.AQUA + "Use /enderchest to open it.");
        return true;
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return false;
    }
}
