package com.github.imdabigboss.easycraft;

import com.github.imdabigboss.easycraft.events.DeathMessages;
import com.github.imdabigboss.easycraft.events.EventListener;
import com.github.imdabigboss.easycraft.events.LobbyEvents;
import com.github.imdabigboss.easycraft.managers.*;
import com.github.imdabigboss.easycraft.perks.PetPerk;
import com.github.imdabigboss.easycraft.perks.RavelPerk;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class EasyCraft extends JavaPlugin {
	private static EasyCraft instance;
	private static Logger logger;

	private static UUIDManager uuidManager = null;
	private static PerksManager perksManager = null;
	private static TpaManager tpaManager = null;
	private static PluginMessageManager pluginMessageManager = null;
	private static CommandManager commandManager = null;
	private static ConfigManager configManager = null;
	private static Ravel1984Manager ravel1984Manager = null;
	private static MiniBlockManager miniBlockManager = null;
	private static LanguageManager languageManager = null;
	private static NPCManager npcManager = null;

	private static boolean easyDatapackInstalled = false;
	private static String serverName = ChatColor.RED + "ERROR";

	@Override
	public void onEnable() {
		instance = this;
		logger = getLogger();

		if (this.getServer().getPluginManager().getPlugin("EasyDatapack") != null) {
			easyDatapackInstalled = true;
		}

		this.saveDefaultConfig();

		configManager = new ConfigManager();

		if (this.getConfig().contains("serverName")) {
			serverName = this.getConfig().getString("serverName");
		} else {
			this.getConfig().set("serverName", serverName);
			this.saveConfig();
		}

		uuidManager = new UUIDManager();
		tpaManager = new TpaManager();
		pluginMessageManager = new PluginMessageManager();
		miniBlockManager = new MiniBlockManager();
		languageManager = new LanguageManager();
		npcManager = new NPCManager();
		npcManager.load();

		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getPluginManager().registerEvents(new DeathMessages(), this);
		if (serverName.equals("lobby")) {
			this.getServer().getPluginManager().registerEvents(new LobbyEvents(), this);
			this.getLogger().info("Lobby mode enabled.");
		}

		if (this.getConfig().contains("enable1984") && this.getConfig().getBoolean("enable1984")) {
			ravel1984Manager = new Ravel1984Manager();
			this.getLogger().info("Ravel 1984 enabled!");
		}

		perksManager = new PerksManager();
		commandManager = new CommandManager();
		commandManager.init();

		pluginMessageManager.initConnection();
	}

	@Override
	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);

		RavelPerk perk = perksManager.getPerk(PetPerk.NAME);
		if (perk != null) {
			PetPerk petPerk = (PetPerk) perk;
			petPerk.removeAllPets();
		}

		if (ravel1984Manager != null) {
			ravel1984Manager.flushCache();
		}

		pluginMessageManager.close();

		npcManager.unload();
	}

	public static EasyCraft getInstance() {
		return instance;
	}

	public static Logger getLog() {
		return logger;
	}

	public static String getServerName() {
		return serverName;
	}

	public static UUIDManager getUUUIDManager() {
		return uuidManager;
	}

	public static PerksManager getPerksManager() {
		return perksManager;
	}

	public static TpaManager getTpaManager() {
		return tpaManager;
	}

	public static PluginMessageManager getPluginMessageManager() {
		return pluginMessageManager;
	}

	public static ConfigManager.YmlConfig getConfig(String name) {
		return configManager.getConfig(name);
	}

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static Ravel1984Manager getRavel1984Manager() {
		return ravel1984Manager;
	}

	public static MiniBlockManager getMiniBlockManager() {
		return miniBlockManager;
	}

	public static LanguageManager getLanguageManager() {
		return languageManager;
	}

	public static NPCManager getNPCManager() {
		return npcManager;
	}

	public static boolean isEasyDatapackInstalled() {
		return easyDatapackInstalled;
	}
}
