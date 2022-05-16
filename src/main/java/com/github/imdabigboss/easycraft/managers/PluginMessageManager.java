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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PluginMessageManager implements PluginMessageListener {
    public static final String CHANNEL_ID = "imdabigboss:main";

    public PluginMessageManager() {
        EasyCraft.getInstance().getServer().getMessenger().registerIncomingPluginChannel(EasyCraft.getInstance(), PluginMessageManager.CHANNEL_ID, this);
        EasyCraft.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(EasyCraft.getInstance(), PluginMessageManager.CHANNEL_ID);
    }

    public void unregister() {
        EasyCraft.getInstance().getServer().getMessenger().unregisterOutgoingPluginChannel(EasyCraft.getInstance());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        //EasyCraft.getLog().info("Plugin Message Received " + channel);

        if (channel.equalsIgnoreCase(CHANNEL_ID)) {
            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("EasyCMD")) {
                String cmd = in.readUTF();
                String data = in.readUTF();
                this.runBungeeCmd(cmd, data);
            }
        }
    }

    private void runBungeeCmd(String cmd, String data) {
        if (cmd.equalsIgnoreCase("setdisplayname")) {
            String[] dataArray = data.split(";");

            UUID uuid = UUID.fromString(dataArray[0]);
            Player player = EasyCraft.getInstance().getServer().getPlayer(uuid);

            String rankName = dataArray[1];
            String color = dataArray[2];

            if (player == null) {
                EasyCraft.getLog().severe("p");
                return;
            }

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

    public void sendCmd(Player player, String cmd, String data){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        try {
            out.writeUTF("EasyCMD");
            out.writeUTF(cmd);
            out.writeUTF(data);
        } catch (IOException ignored) {}

        player.sendPluginMessage(EasyCraft.getInstance(), CHANNEL_ID, bytes.toByteArray());
        //EasyCraft.getLog().info("Sent plugin message to " + player.getName() + ": " + cmd + " " + data);

        try {
            out.close();
            bytes.close();
        } catch (IOException ignored) {}
    }
}
