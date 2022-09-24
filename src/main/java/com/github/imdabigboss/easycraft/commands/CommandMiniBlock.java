package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.MiniBlockManager;
import com.github.imdabigboss.easycraft.utils.HeadUtils;
import de.themoep.inventorygui.*;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CommandMiniBlock implements CommandExecutor, MiniBlockManager.OnPlayerChooseMiniBlock {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			EasyCraft.getMiniBlockManager().playerChoseMiniBlcok(player, this);
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
		}
		return true;
	}

	@Override
	public void onPlayerChooseMiniBlock(Player player, InventoryGui gui, GuiElement.Click click) {
		ItemStack currentItem = click.getEvent().getCurrentItem();
		if (currentItem != null) {
			player.getInventory().addItem(click.getEvent().getCurrentItem());
			player.updateInventory();
			player.sendMessage(ChatColor.AQUA + "You have been given the mini block.");
		} else {
			player.sendMessage(ChatColor.RED + "Something went wrong.");
		}
	}
}
