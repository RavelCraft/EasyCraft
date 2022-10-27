package com.github.imdabigboss.easycraft.perks;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easydatapack.api.EasyDatapackAPI;
import com.github.imdabigboss.easydatapack.api.items.CustomHatItem;
import com.github.imdabigboss.easydatapack.api.items.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class HatPerk extends RavelPerk {
    public static final String NAME = "hat";

    public Map<String, ItemStack> hats = new HashMap<>();

    public HatPerk() {
        super(NAME, 2, false);

        if (EasyCraft.isEasyDatapackInstalled()) {
            for (CustomItem item : EasyDatapackAPI.getItemManager().getCustomItems()) {
                if (item instanceof CustomHatItem) {
                    this.hats.put(item.getNamespaceKey(), item.getItemStack());
                }
            }
        }
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (!EasyCraft.isEasyDatapackInstalled()) {
            player.sendMessage(ChatColor.RED + "Something is strange, please contact a member of staff.");
            return false;
        }

        if (args.length == 0) {
            StringBuilder sb = new StringBuilder("Please specify a hat from the list below:");
            for (String hat : this.hats.keySet()) {
                sb.append("\n - ").append(hat);
            }

            player.sendMessage(ChatColor.RED + sb.toString());
            return false;
        } else if (args.length > 1) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
        }

        if (!this.hats.containsKey(args[0])) {
            player.sendMessage(ChatColor.RED + "That hat does not exist!");
            return false;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You have no space in your inventory!");
            return false;
        } else {
            player.getInventory().addItem(this.hats.get(args[0]).clone());
        }

        return true;
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return false;
    }
}
