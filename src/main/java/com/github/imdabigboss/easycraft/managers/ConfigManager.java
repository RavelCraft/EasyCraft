package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private final EasyCraft plugin = EasyCraft.getInstance();
    private final Map<String, YmlConfig> configs = new HashMap<>();

    public ConfigManager() {
        YmlConfig config = new YmlConfig(this.plugin.getConfig());
        config.saveConfig();

        configs.put("config", config);
    }

    public YmlConfig getConfig(String name) {
        if (configs.containsKey(name)) {
            return configs.get(name);
        } else {
            YmlConfig config = new YmlConfig(this.plugin, name + ".yml");
            config.saveConfig();

            configs.put(name, config);

            return config;
        }
    }

    public YmlConfig createConfig(File file, String name) {
        if (configs.containsKey(name)) {
            return null;
        } else {
            YmlConfig config = new YmlConfig(this.plugin, file);
            config.saveConfig();

            configs.put(name, config);

            return config;
        }
    }

    public void saveConfigs() {
        for (YmlConfig config : configs.values()) {
            config.saveConfig();
        }
    }

    public static class YmlConfig {
        private FileConfiguration configuration;
        private File file = null;

        public YmlConfig(EasyCraft plugin, File file) {
            this.file = file;
            if (!this.file.exists()) {
                plugin.saveResource(file.getName(), false);
            }

            this.configuration = new YamlConfiguration();

            try {
                this.configuration.load(this.file);
            } catch (IOException | InvalidConfigurationException e) {
                EasyCraft.getLog().severe("Unable to create config file " + file.getAbsolutePath() + ": " + e.getMessage());
            }
        }

        public YmlConfig(EasyCraft plugin, String configName) {
            this(plugin, new File(plugin.getDataFolder(), configName));
        }

        public YmlConfig(FileConfiguration config) {
            this.configuration = config;
        }

        public FileConfiguration getConfig() {
            return configuration;
        }

        public void saveConfig() {
            if (file == null) {
                EasyCraft.getInstance().saveConfig();
            } else {
                try {
                    configuration.save(file);
                } catch (IOException e) {
                    EasyCraft.getLog().severe("Unable to save config file " + file.getAbsolutePath() + ": " + e.getMessage());
                }
            }
        }

        public void reloadConfig() {
            if (file != null) {
                this.configuration = new YamlConfiguration();

                try {
                    this.configuration.load(this.file);
                } catch (IOException | InvalidConfigurationException e) {
                    EasyCraft.getLog().severe("Unable to create config file " + file.getAbsolutePath() + ": " + e.getMessage());
                }
            }
        }
    }
}
