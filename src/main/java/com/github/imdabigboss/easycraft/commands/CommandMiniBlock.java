package com.github.imdabigboss.easycraft.commands;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.managers.MiniBlockManager;
import com.github.imdabigboss.easycraft.utils.PlayerMessage;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandMiniBlock implements CommandExecutor, MiniBlockManager.OnPlayerChooseMiniBlock {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			EasyCraft.getMiniBlockManager().playerChoseMiniBlcok(player, this);
		} else {
			sender.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MUST_BE_PLAYER, sender));
		}
		return true;
	}

	@Override
	public void onPlayerChooseMiniBlock(Player player, InventoryGui gui, GuiElement.Click click) {
		ItemStack currentItem = click.getEvent().getCurrentItem();
		if (currentItem != null) {
			player.getInventory().addItem(click.getEvent().getCurrentItem());
			player.updateInventory();
			player.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MINIBLOCK_GIVEN, player));
		} else {
			player.sendMessage(PlayerMessage.formatMessage(PlayerMessage.COMMAND_MINIBLOCK_ERROR, player));
		}
	}
}
