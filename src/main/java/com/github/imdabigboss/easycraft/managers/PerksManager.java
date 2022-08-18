package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.perks.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PerksManager {
	private final EasyCraft plugin = EasyCraft.getInstance();
	private final ConfigManager.YmlConfig config = EasyCraft.getConfig("perks");

	private Map<String, RavelPerk> perks = new HashMap<>();

	public PerksManager() {
		this.registerPerk(new MagicStickPerk());
		this.registerPerk(new EnderchestPerk());
		this.registerPerk(new SuperCatPerk());
		this.registerPerk(new SuperDogPerk());
		this.registerPerk(new ArrivalAnouncePerk());
		this.registerPerk(new FireworkLauncher());
		this.registerPerk(new CapePerk());
	}

	private void registerPerk(RavelPerk perk) {
		if (this.perks.containsKey(perk.getName())) {
			this.plugin.getLogger().severe("Perk " + perk.getName() + " already exists, was not added!");
			return;
		}

		this.perks.put(perk.getName(), perk);
	}

	public int getPlayerLevel(UUID uuid) {
		if (this.config.getConfig().contains(uuid + ".level")) {
			return this.config.getConfig().getInt(uuid + ".level");
		}

		return 0;
	}

	public void setPlayerLevel(UUID uuid, int level) {
		this.config.getConfig().set(uuid + ".level", level);
		this.config.saveConfig();
	}

	public List<String> getPlayerPerks(UUID uuid) {
		if (this.config.getConfig().contains(uuid + ".perks")) {
			return this.config.getConfig().getStringList(uuid + ".perks");
		}

		return new ArrayList<>();
	}

	public boolean playerHasPerk(UUID uuid, String perk) {
		return this.getPlayerPerks(uuid).contains(perk);
	}

	public boolean playerGetPerk(Player player, String[] args) {
		if (args.length == 0) {
			return false;
		}

		if (this.perks.containsKey(args[0])) {
			RavelPerk perk = this.perks.get(args[0]);

			int playerLevel = this.getPlayerLevel(player.getUniqueId());
			if (playerLevel < perk.getLevel() && playerLevel != -1) {
				player.sendMessage(ChatColor.RED + "You do not have the required level to get this perk!");
				return true;
			}

			boolean alreadyHadPerk = false;
			if (this.playerHasPerk(player.getUniqueId(), perk.getName())) {
				if (!perk.canBuyMultiple()) {
					if (playerLevel == -1) {
						player.sendMessage(ChatColor.YELLOW + "This perk is only meant to be purchased once!");
					} else {
						player.sendMessage(ChatColor.RED + "You already have this perk!");
						return true;
					}
				} else {
					alreadyHadPerk = true;
				}
			}

			boolean didGet = perk.getPerk(player, Arrays.copyOfRange(args, 1, args.length));
			if (didGet) {
				player.sendMessage(ChatColor.AQUA + "You have purchased the perk " + ChatColor.GOLD + perk.getName() + ChatColor.AQUA + "!");

				if (!alreadyHadPerk) {
					List<String> playerPerks = this.getPlayerPerks(player.getUniqueId());
					playerPerks.add(perk.getName());
					this.config.getConfig().set(player.getUniqueId() + ".perks", playerPerks);
					this.config.saveConfig();
				}
			} else {
				player.sendMessage(ChatColor.RED + "Perk purchase failed.");
			}
		}

		return true;
	}

	public RavelPerk getPerk(String perkName) {
		return this.perks.getOrDefault(perkName, null);
	}

	public List<RavelPerk> listPerks() {
		return new ArrayList<>(this.perks.values());
	}

	public List<String> listPerksString() {
		List<String> perks = new ArrayList<>();

		for (RavelPerk perk : this.listPerks()) {
			perks.add(perk.getName());
		}

		return perks;
	}

	public List<String> listPerksAndLevels() {
		List<String> perks = new ArrayList<>();

		for (RavelPerk perk : this.listPerks()) {
			perks.add(perk.getName() + ", Donator: " + perk.getLevel());
		}

		return perks;
	}

	public boolean isUndroppable(ItemStack item) {
		for (RavelPerk perk : this.listPerks()) {
			if (perk.isUndroppable(item)) {
				return true;
			}
		}

		return false;
	}
}
