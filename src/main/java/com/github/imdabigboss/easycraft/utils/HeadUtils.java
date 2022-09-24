package com.github.imdabigboss.easycraft.utils;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.Base64;
import java.util.UUID;

public class HeadUtils {
    private HeadUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static ItemStack getHead(Player player) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta sm = (SkullMeta) skull.getItemMeta();
        OfflinePlayer p = EasyCraft.getInstance().getServer().getOfflinePlayer(player.getUniqueId());
        sm.setOwningPlayer(p);
        skull.setItemMeta(sm);
        return skull;
    }

    public static ItemStack getHeadFromBase64(String base64) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (base64.isEmpty()) {
            return head;
        }

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        profile.getProperties().put("textures", new Property("textures", base64));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            EasyCraft.getLog().severe("Failed to set profile for head item: " + e.getMessage());
        }
        head.setItemMeta(skullMeta);
        return head;
    }

    public static ItemStack getHead(String url) {
        if (url.isEmpty()) {
            return new ItemStack(Material.PLAYER_HEAD);
        }

        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        return getHeadFromBase64(new String(encodedData));
    }
}
