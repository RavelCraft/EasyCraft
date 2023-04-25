package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.NPCManager;
import dev.sergiferry.playernpc.api.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandNPC implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            this.sendHelp(sender);
            return true;
        }

        NPCManager manager = EasyCraft.getNPCManager();

        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "The ID must be a number!");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
                return true;
            }

            if (args.length < 4) {
                this.sendHelp(sender);
                return true;
            }

            Player player = (Player) sender;

            String text = args[2];
            String skin = args[3];
            Location location = player.getLocation();
            NPC.Pose pose = null;
            NPCManager.ActionType actionType = null;
            String action = null;

            try {
                if (args.length == 5) {
                    pose = NPC.Pose.valueOf(args[4]);
                } else if (args.length == 6) {
                    actionType = NPCManager.ActionType.valueOf(args[4]);
                    action = args[5];
                } else if (args.length == 7) {
                    pose = NPC.Pose.valueOf(args[4]);
                    actionType = NPCManager.ActionType.valueOf(args[5]);
                    action = args[6];
                }
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Invalid pose or action type!");
                return true;
            }

            text = this.textProcess(text);
            action = this.textProcess(action);

            if (manager.createNPC(id, text, skin, location, pose, actionType, action)) {
                sender.sendMessage(ChatColor.AQUA + "NPC created!");
            } else {
                sender.sendMessage(ChatColor.RED + "An NPC with that ID already exists!");
            }
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                this.sendHelp(sender);
                return true;
            }

            if (manager.removeNPC(id)) {
                sender.sendMessage(ChatColor.AQUA + "NPC deleted!");
            } else {
                sender.sendMessage(ChatColor.RED + "An NPC with that ID does not exist!");
            }
        } else {
            this.sendHelp(sender);
        }

        return true;
    }

    private String textProcess(String text) {
        if (text == null) {
            return null;
        }

        return ChatColor.translateAlternateColorCodes('&', text.replace("_", " ").replace("|", "\n"));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("""
                Here is the correct usage:
                 - create <id> <text> <skin>
                 - create <id> <text> <skin> <pose>
                 - create <id> <text> <skin> <actionType> <action>
                 - create <id> <text> <skin> <pose> <actionType> <action>
                 - delete <id>""");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> cmds = new ArrayList<>();
        if (args.length == 1) {
            cmds.add("create");
            cmds.add("delete");
        } else if (args.length == 5) {
            for (NPC.Pose pose : NPC.Pose.values()) {
                cmds.add(pose.name());
            }
            for (NPCManager.ActionType actionType : NPCManager.ActionType.values()) {
                cmds.add(actionType.name());
            }
        } else if (args.length == 6) {
            for (NPCManager.ActionType actionType : NPCManager.ActionType.values()) {
                cmds.add(actionType.name());
            }
        }
        return cmds;
    }
}
