package com.github.imdabigboss.easycraft.perks;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MagicStickPerk extends RavelPerk {
    public static final String NAME = "magicstick";
    public static final int CUSTOM_MODEL_DATA = 502698;

    public MagicStickPerk() {
        super(NAME, 1, true);
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
        }

        ItemStack item = new ItemStack(Material.STICK);
        item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
        item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);

        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(ChatColor.AQUA + "Magic Stick"));
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
}
