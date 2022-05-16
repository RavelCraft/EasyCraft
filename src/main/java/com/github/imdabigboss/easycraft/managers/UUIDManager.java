package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.uuid.UUIDFetcher;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class UUIDManager {
    private final ConfigManager.YmlConfig uuidConfig = EasyCraft.getConfig("uuid");

    private final String bedrock = "bedrock.";
    private final String java = "java.";

    public void registerUUID(UUID uuid, String playerName) {
        if (playerName.startsWith(".")) {
            this.uuidConfig.reloadConfig();

            this.uuidConfig.getConfig().set(this.bedrock + playerName.substring(1), uuid.toString());
            this.uuidConfig.saveConfig();
        } else {
            this.uuidConfig.reloadConfig();

            this.uuidConfig.getConfig().set(this.java + playerName, uuid.toString());
            this.uuidConfig.saveConfig();
        }
    }

    public UUID playerNameToUUID(String playerName) {
        Player player = EasyCraft.getInstance().getServer().getPlayer(playerName);
        if (player != null) {
            this.registerUUID(player.getUniqueId(), playerName);
            return player.getUniqueId();
        }

        String tmpName;
        if (playerName.startsWith(".")) {
            tmpName = this.bedrock + playerName.substring(1);
        } else {
            tmpName = this.java + playerName;
        }

        this.uuidConfig.reloadConfig();
        if (this.uuidConfig.getConfig().contains(tmpName)) {
            return UUID.fromString(this.uuidConfig.getConfig().getString(tmpName));
        }


        UUID uuid = UUIDFetcher.getUUID(playerName);
        if (uuid != null) {
            this.registerUUID(uuid, playerName);

            return uuid;
        }

        return null;
    }

    public String getPlayerName(UUID uuid) {
        Player player = EasyCraft.getInstance().getServer().getPlayer(uuid);
        if (player != null) {
            this.registerUUID(player.getUniqueId(), player.getName());
            return player.getName();
        }

        this.uuidConfig.reloadConfig();
        Set<String> playerNames = this.uuidConfig.getConfig().getKeys(true);
        for (String playerName : playerNames) {
            String tmp = playerName + ".";
            if (tmp.equals(this.bedrock) || tmp.equals(this.java)) {
                continue;
            }

            String uuidString = this.uuidConfig.getConfig().getString(playerName);
            if (uuidString.equals(uuid.toString())) {
                if (playerName.startsWith(this.bedrock)) {
                    return playerName.substring(7);
                } else if (playerName.startsWith(this.java)) {
                    return playerName.substring(5);
                } else {
                    EasyCraft.getLog().severe("Something is wrong in the UUID config!!! " + playerName);
                    return null;
                }
            }
        }

        String name = UUIDFetcher.getName(uuid);
        if (name != null) {
            this.registerUUID(uuid, name);

            return name;
        }

        return null;
    }
}
