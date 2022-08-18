package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class PluginMessageManager implements PluginMessageListener {
    private static final String CHANNEL_ID = "imdabigboss:main";
    private static final String CHANNEL_NAME = "EasyCMD";

    private final Queue<PluginMessageData> pluginMessageQueue = new LinkedList<>();

    public PluginMessageManager() {
        EasyCraft.getInstance().getServer().getMessenger().registerIncomingPluginChannel(EasyCraft.getInstance(), CHANNEL_ID, this);
        EasyCraft.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(EasyCraft.getInstance(), CHANNEL_ID);
    }

    public void unregister() {
        EasyCraft.getInstance().getServer().getMessenger().unregisterOutgoingPluginChannel(EasyCraft.getInstance());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (channel.equalsIgnoreCase(CHANNEL_ID)) {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase(CHANNEL_NAME)) {
                String cmd = in.readUTF();
                String data = in.readUTF();
                UUID uuid = UUID.fromString(in.readUTF());

                PluginMessageData messageData = new PluginMessageData(cmd, data, uuid);
                Player target = EasyCraft.getInstance().getServer().getPlayer(uuid);
                if (target == null) {
                    this.pluginMessageQueue.add(messageData);
                } else {
                    this.runVelocityCmd(messageData);
                }
            }
        }
    }

    private void runVelocityCmd(PluginMessageData data) {
        if (data.cmd().equalsIgnoreCase("setdisplayname")) {
            if (data.cmd().equalsIgnoreCase("setdisplayname")) {
                Player player = EasyCraft.getInstance().getServer().getPlayer(data.uuid());

                String[] dataArray = data.data().split(";");
                String rankName = dataArray[0];
                String color = dataArray[1];

                String prefix = "";
                if (rankName.equals("none")) {
                    player.displayName(Component.text(player.getName()));
                    player.customName(Component.text(player.getName()));
                    player.setCustomNameVisible(false);
                } else {
                    prefix = ChatColor.WHITE + "[" + color + rankName + ChatColor.WHITE + "] ";
                    player.displayName(Component.text(prefix + player.getName()));
                    player.customName(Component.text(prefix + player.getName()));
                    player.setCustomNameVisible(true);
                }

                ScoreboardManager scoreboardManager = EasyCraft.getInstance().getServer().getScoreboardManager();
                Scoreboard scoreboard = scoreboardManager.getMainScoreboard();

                boolean teamExists = false;
                Team playerTeam = null;
                for (Team team : scoreboard.getTeams()) {
                    if (team.getName().equalsIgnoreCase(rankName)) {
                        teamExists = true;
                        playerTeam = team;
                    }

                    if (team.hasEntry(player.getName())) {
                        team.removeEntry(player.getName());
                    }
                }

                if (rankName.equals("none")) {
                    return;
                }

                if (!teamExists) {
                    playerTeam = scoreboard.registerNewTeam(rankName);
                }

                playerTeam.color(NamedTextColor.WHITE);
                playerTeam.setAllowFriendlyFire(true);
                playerTeam.prefix(Component.text(prefix));
                playerTeam.addEntry(player.getName());
            }
        }
    }

    public void readQueuedPluginMessages() {
        while (!this.pluginMessageQueue.isEmpty()) {
            PluginMessageManager.PluginMessageData data = this.pluginMessageQueue.poll();
            this.runVelocityCmd(data);
        }
    }

    private static class PluginMessageData {
        private final String cmd;
        private final String data;
        private final UUID uuid;

        public PluginMessageData(String cmd, String data, UUID uuid) {
            this.cmd = cmd;
            this.data = data;
            this.uuid = uuid;
        }

        public String cmd() {
            return this.cmd;
        }

        public String data() {
            return this.data;
        }

        public UUID uuid() {
            return this.uuid;
        }
    }
}
