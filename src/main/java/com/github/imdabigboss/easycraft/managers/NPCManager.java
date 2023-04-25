package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import dev.sergiferry.playernpc.api.NPC;
import dev.sergiferry.playernpc.api.NPCLib;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class NPCManager {
    private final ConfigManager.YmlConfig config = EasyCraft.getConfig("npcs");
    private final NPCLib npcLib = NPCLib.getInstance();
    private final EasyCraft plugin = EasyCraft.getInstance();

    private final Map<Integer, NPC.Global> npcs = new HashMap<>();

    public NPCManager() {
        this.npcLib.registerPlugin(this.plugin);
    }

    public boolean createNPC(int id, String text, String skin, Location location, NPC.Pose pose, ActionType actionType, String action) {
        if (this.npcs.containsKey(id)) {
            return false;
        }

        String identifier = "easynpc_" + id;

        NPC.Global npc = this.npcLib.generateGlobalNPC(this.plugin, identifier, location);
        this.npcs.put(id, npc);

        npc.setSkin(skin);
        npc.setText(text.split("\n"));
        npc.setGlowing(false);
        npc.setCollidable(false);
        npc.setGazeTrackingType(NPC.GazeTrackingType.NONE);
        npc.setTabListVisibility(NPC.TabListVisibility.NEVER);
        npc.setVisibility(NPC.Global.Visibility.EVERYONE);

        this.config.getConfig().set(identifier + ".text", text);
        this.config.getConfig().set(identifier + ".skin", skin);
        this.config.getConfig().set(identifier + ".location", location);

        if (pose != null) {
            this.config.getConfig().set(identifier + ".pose", pose.name());
            npc.setPose(pose);
        }

        if (actionType != null && action != null) {
            this.config.getConfig().set(identifier + ".actionType", actionType.name());
            this.config.getConfig().set(identifier + ".action", action);

            switch (actionType) {
                case COMMAND -> npc.addRunConsoleCommandClickAction(action);
                case MESSAGE -> npc.addMessageClickAction(action);
                case SERVER -> npc.addConnectBungeeServerClickAction(action);
                case PLAYER_COMMAND -> npc.addRunPlayerCommandClickAction(action);
                default -> throw new RuntimeException("Invalid ActionType: " + actionType);
            }
        }

        this.config.saveConfig();

        npc.createAllPlayers();
        npc.show();

        npc.forceUpdateText();
        npc.forceUpdate();

        return true;
    }

    public boolean createNPC(int id, String text, String skin, Location location, NPC.Pose pose) {
        return this.createNPC(id, text, skin, location, pose, null, null);
    }

    public boolean createNPC(int id, String text, String skin, Location location, ActionType actionType, String action) {
        return this.createNPC(id, text, skin, location, null, actionType, action);
    }

    public boolean createNPC(int id, String text, String skin, Location location) {
        return this.createNPC(id, text, skin, location, null, null, null);
    }

    public boolean removeNPC(int id) {
        NPC.Global npc = this.npcs.remove(id);
        if (npc == null) {
            return false;
        }

        npc.hide();
        npc.destroy();

        this.npcs.remove(id);

        this.config.getConfig().set("easynpc_" + id, null);
        this.config.saveConfig();

        return true;
    }

    public void load() {
        for (String key : this.config.getConfig().getKeys(false)) {
            if (!key.startsWith("easynpc_")) {
                continue;
            }

            int id = Integer.parseInt(key.replace("easynpc_", ""));

            String text = this.config.getConfig().getString(key + ".text");
            if (text == null) {
                continue;
            }

            String skin = this.config.getConfig().getString(key + ".skin");
            Location location = (Location) this.config.getConfig().get(key + ".location");
            NPC.Pose pose = null;
            ActionType actionType = null;
            String action = null;

            if (this.config.getConfig().contains(key + ".pose")) {
                pose = NPC.Pose.valueOf(this.config.getConfig().getString(key + ".pose"));
            }

            if (this.config.getConfig().contains(key + ".actionType") && this.config.getConfig().contains(key + ".action")) {
                actionType = ActionType.valueOf(this.config.getConfig().getString(key + ".actionType"));
                action = this.config.getConfig().getString(key + ".action");
            }

            this.config.saveConfig();

            this.createNPC(id, text, skin, location, pose, actionType, action);
        }
    }

    public void unload() {
        for (NPC.Global npc : this.npcs.values()) {
            npc.hide();
            npc.destroy();
        }
    }

    public enum ActionType {
        COMMAND,
        PLAYER_COMMAND,
        MESSAGE,
        SERVER
    }
}
