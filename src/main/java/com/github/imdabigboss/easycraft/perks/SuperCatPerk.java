package com.github.imdabigboss.easycraft.perks;

import com.github.imdabigboss.easycraft.EasyCraft;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SuperCatPerk extends RavelPerk {
    public static final String NAME = "supercat";

    private final EasyCraft plugin = EasyCraft.getInstance();

    public SuperCatPerk() {
        super(NAME, 3, true);
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Please specify a name");
            return false;
        } else if (args.length > 1) {
            player.sendMessage(ChatColor.RED + "Too many arguments");
            return false;
        }

        String command = "give \"" + player.getName() + "\" cat_spawn_egg{EntityTag:{id:\"minecraft:cat\",CustomName:\"\\\"" + args[0] + "\\\"\",CustomNameVisible:1,Invulnerable:1,Age:0,Owner:\"" + player.getName() + "\"}}";
        this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command);

        return true;
    }
}
