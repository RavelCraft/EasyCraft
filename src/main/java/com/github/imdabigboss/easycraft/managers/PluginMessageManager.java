package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PluginMessageManager {
    private static final int PLUGIN_MESSAGE_PORT = 17157;

    private boolean listening = true;
    private Socket socket = null;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public void initConnection() {
        if (this.socket != null) {
            return;
        }

        new Thread(() -> {
            try {
                this.listening = true;
                this.socket = new Socket("localhost", PLUGIN_MESSAGE_PORT);
            } catch (IOException e) {
                EasyCraft.getLog().warning("Unable to connect to plugin messaging");
                return;
            }

            try {
                this.outputStream = new DataOutputStream(this.socket.getOutputStream());
                this.inputStream = new DataInputStream(this.socket.getInputStream());

                this.inputStream.readUTF();

                this.outputStream.writeUTF(EasyCraft.getServerName());
                this.outputStream.flush();

                if (!this.inputStream.readBoolean()) {
                    EasyCraft.getLog().severe("Server didn't recognize plugin messaging connection");
                    this.close();
                    return;
                }
            } catch (IOException e) {
                EasyCraft.getLog().warning("Failed to initialize plugin messaging connection");
                this.close();
                return;
            }

            EasyCraft.getLog().info("Connected to plugin messaging");

            try {
                while (this.listening) {
                    String command = this.inputStream.readUTF();

                    List<String> args = new ArrayList<>();
                    while (true) {
                        String line = this.inputStream.readUTF();
                        if (line.equals("\u0000")) {
                            break;
                        }

                        args.add(line);
                    }

                    this.runCommand(command, args.toArray(new String[0]));
                }
            } catch (IOException e) {
                EasyCraft.getLog().warning("Disconnected from plugin messaging");
            }

            this.close();
        }).start();
    }

    public void close() {
        this.listening = false;
        if (this.socket == null) {
            return;
        }

        try {
            InputStream inputStream = this.socket.getInputStream();
            OutputStream outputStream = this.socket.getOutputStream();
            this.socket.close();

            inputStream.close();
            this.inputStream.close();

            outputStream.close();
            this.outputStream.close();
        } catch (IOException e) {
            EasyCraft.getLog().severe("Unable to close plugin messaging socket");
        }

        this.socket = null;
    }

    private void runCommand(String command, String[] args) throws IOException {
        if (command.equalsIgnoreCase("setdisplayname")) {
            UUID uuid;
            try {
                uuid = UUID.fromString(args[0]);
            } catch (IllegalArgumentException e) {
                EasyCraft.getLog().severe("Got invalid UUID from plugin messaging in setdisplayname command");
                return;
            }
            Player player = EasyCraft.getInstance().getServer().getPlayer(uuid);

            String rankName = args[1];
            String color = args[2];

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
