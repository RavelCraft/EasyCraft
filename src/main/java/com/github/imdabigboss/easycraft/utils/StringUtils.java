package com.github.imdabigboss.easycraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;

public class StringUtils {
    public static String componentToString(Component text) {
        if (text == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        if (text.children().isEmpty()) {
            sb.append(((TextComponent) text.compact()).content());
        }

        for (Component component : text.children()) {
            if (component.compact().style().color() != null) {
                sb.append("&").append((component.compact()).style().color().asHexString())
                        .append(((TextComponent) component.compact()).content());
            } else {
                System.out.println(((TextComponent) component.compact()).content());
                sb.append(((TextComponent) component.compact()).content());
            }
        }
        return sb.toString();
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + ": " + location.getX() + " " + location.getY() + " " + location.getZ();
    }
}
