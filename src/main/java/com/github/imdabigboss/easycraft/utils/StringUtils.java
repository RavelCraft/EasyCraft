package com.github.imdabigboss.easycraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;

public class StringUtils {
    public static String componentToString(Component text) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();
        return serializer.serialize(text);
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + ": " + location.getX() + " " + location.getY() + " " + location.getZ();
    }
}
