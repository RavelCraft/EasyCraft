package com.github.imdabigboss.easycraft.perks;

import com.connexal.raveldatapack.RavelDatapack;
import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpeedBoostPerk extends RavelPerk {
    public static final String NAME = "speed_boost";

    public ItemStack item = null;

    public SpeedBoostPerk() {
        super(NAME, 2, false);

        if (EasyCraft.isRavelDatapackInstalled()) {
            this.item = RavelDatapack.getItemManager().getItem("speed_boost");
        }
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (this.item == null) {
            player.sendMessage(ChatColor.RED + "Something is strange, please contact a member of staff.");
            return false;
        }

        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You have no space in your inventory!");
            return false;
        } else {
            player.getInventory().addItem(this.item.clone());
        }

        return true;
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return false;
    }
}
