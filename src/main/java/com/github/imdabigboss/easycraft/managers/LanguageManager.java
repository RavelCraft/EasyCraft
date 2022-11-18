package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.utils.PlayerMessage;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LanguageManager {
    private final Map<UUID, PlayerMessage.MessageLanguage> languages = new HashMap<>();

    public void setPlayerLanguage(Player player, PlayerMessage.MessageLanguage language) {
        this.languages.remove(player.getUniqueId());
        this.languages.put(player.getUniqueId(), language);
    }

    public PlayerMessage.MessageLanguage getPlayerLanguage(Player player) {
        return this.languages.getOrDefault(player.getUniqueId(), null);
    }
}