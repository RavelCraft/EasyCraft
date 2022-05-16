package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.commands.*;
import com.github.imdabigboss.easycraft.commands.home.CommandDelHome;
import com.github.imdabigboss.easycraft.commands.home.CommandHome;
import com.github.imdabigboss.easycraft.commands.home.CommandSetHome;
import com.github.imdabigboss.easycraft.commands.tpa.CommandTpa;
import com.github.imdabigboss.easycraft.commands.tpa.CommandTpaccept;
import com.github.imdabigboss.easycraft.commands.tpa.CommandTpahere;
import com.github.imdabigboss.easycraft.commands.tpa.CommandTpdeny;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandManager {
    private boolean homesEnabled = true;
    private boolean spawnEnabled = true;
    private boolean tpaEnabled = true;
    private boolean suicideEnabled = true;
    private boolean headitemEnabled = true;
    private boolean miniblocksEnabled = true;
    private boolean dropPlayerHeadOnKillEnabled = true;

    public void init() {
        EasyCraft plugin = EasyCraft.getInstance();
        FileConfiguration config = plugin.getConfig();

        if (config.contains("commands.homes")) {
            this.homesEnabled = config.getBoolean("commands.homes");
        }
        if (config.contains("commands.spawn")) {
            this.spawnEnabled = config.getBoolean("commands.spawn");
        }
        if (config.contains("commands.tpa")) {
            this.tpaEnabled = config.getBoolean("commands.tpa");
        }
        if (config.contains("commands.suicide")) {
            this.suicideEnabled = config.getBoolean("commands.suicide");
        }
        if (config.contains("commands.headitem")) {
            this.headitemEnabled = config.getBoolean("commands.headitem");
        }
        if (config.contains("commands.miniblocks")) {
            this.miniblocksEnabled = config.getBoolean("commands.miniblocks");
        }
        if (config.contains("dropPlayerHeadOnKill")) {
            this.dropPlayerHeadOnKillEnabled = config.getBoolean("dropPlayerHeadOnKill");
        }

        plugin.getCommand("maintenance").setExecutor(new CommandMaintenance());
        plugin.getCommand("setmaxplayers").setExecutor(new CommandSetMaxPlayers());
        plugin.getCommand("confupdate").setExecutor(new CommandConfUpdate());
        plugin.getCommand("perks").setExecutor(new CommandPerks());
        plugin.getCommand("enderchest").setExecutor(new CommandEnderchest());

        //Optional Commands
        CommandExecutor noCommand = new NoCommand();

        CommandExecutor miniBlocksCommand;
        if (this.miniblocksEnabled) {
            miniBlocksCommand = new CommandMiniBlock();
        } else {
            miniBlocksCommand = noCommand;
        }
        plugin.getCommand("miniblock").setExecutor(miniBlocksCommand);

        CommandExecutor suicideCommand;
        if (this.suicideEnabled) {
            suicideCommand = new CommandSuicide();
        } else {
            suicideCommand = noCommand;
        }
        plugin.getCommand("suicide").setExecutor(suicideCommand);

        CommandExecutor spawnCommand;
        if (this.spawnEnabled) {
            spawnCommand = new CommandSpawn();
        } else {
            spawnCommand = noCommand;
        }
        plugin.getCommand("spawn").setExecutor(spawnCommand);

        CommandExecutor headItemCommand;
        if (this.headitemEnabled) {
            headItemCommand = new CommandHeadItem();
        } else {
            headItemCommand = noCommand;
        }
        plugin.getCommand("headitem").setExecutor(headItemCommand);

        CommandExecutor tpaCommand;
        CommandExecutor tpahereCommand;
        CommandExecutor tpacceptCommand;
        CommandExecutor tpdenyCommand;
        if (this.tpaEnabled) {
            tpaCommand = new CommandTpa();
            tpahereCommand = new CommandTpahere();
            tpacceptCommand = new CommandTpaccept();
            tpdenyCommand = new CommandTpdeny();
        } else {
            tpaCommand = noCommand;
            tpahereCommand = noCommand;
            tpacceptCommand = noCommand;
            tpdenyCommand = noCommand;
        }
        plugin.getCommand("tpa").setExecutor(tpaCommand);
        plugin.getCommand("tpahere").setExecutor(tpahereCommand);
        plugin.getCommand("tpaccept").setExecutor(tpacceptCommand);
        plugin.getCommand("tpdeny").setExecutor(tpdenyCommand);

        CommandExecutor homeCommand;
        CommandExecutor setHomeCommand;
        CommandExecutor delHomeCommand;
        if (this.homesEnabled) {
            homeCommand = new CommandHome();
            setHomeCommand = new CommandSetHome();
            delHomeCommand = new CommandDelHome();
        } else {
            homeCommand = noCommand;
            setHomeCommand = noCommand;
            delHomeCommand = noCommand;
        }
        plugin.getCommand("home").setExecutor(homeCommand);
        plugin.getCommand("sethome").setExecutor(setHomeCommand);
        plugin.getCommand("delhome").setExecutor(delHomeCommand);
    }

    public boolean isHomesEnabled() {
        return this.homesEnabled;
    }
    public boolean isSpawnEnabled() {
        return this.spawnEnabled;
    }
    public boolean isTpaEnabled() {
        return this.tpaEnabled;
    }
    public boolean isSuicideEnabled() {
        return this.suicideEnabled;
    }
    public boolean isHeadItemEnabled() {
        return this.headitemEnabled;
    }
    public boolean isMiniBlocksEnabled() {
        return this.miniblocksEnabled;
    }
    public boolean isDropPlayerHeadOnKillEnabled() {
        return this.dropPlayerHeadOnKillEnabled;
    }
}
