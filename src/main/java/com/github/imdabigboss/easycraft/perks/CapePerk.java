package com.github.imdabigboss.easycraft.perks;

import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Files;
import java.nio.file.Path;

public class CapePerk extends RavelPerk {
    public static final String NAME = "cape";

    private final Path capesPath;

    public CapePerk() {
        super(NAME, 2, false);

        if (EasyCraft.getInstance().getConfig().contains("capes-path")) {
            this.capesPath = EasyCraft.getInstance().getDataFolder().toPath().resolve(EasyCraft.getInstance().getConfig().getString("capes-path"));
        } else {
            this.capesPath = null;
        }
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (Files.exists(this.capesPath.resolve(player.getUniqueId() + ".png"))) {
            player.sendMessage(ChatColor.AQUA + "You have a cape, to change it please contact a member of staff.");
        } else {
            player.sendMessage(ChatColor.AQUA + "Please contact a member of staff to get a cape.");
        }
        return false;
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return false;
    }
}
