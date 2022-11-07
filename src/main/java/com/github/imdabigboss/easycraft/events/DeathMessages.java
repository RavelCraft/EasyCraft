package com.github.imdabigboss.easycraft.events;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.HeadUtils;
import com.github.imdabigboss.easycraft.utils.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DeathMessages implements Listener {
	private final Map<String, Entity> lastDamager = new HashMap<>();
	private static final Random random = new Random();

	private static final String[] playerMessages = new String[] {"%player% was slaughtered by %killer%", "%killer% skilfully killed %player%", "%player% died to the hand of %killer%"};
	private static final String[] arrowMessages = new String[] {"%player% was impaled by %killer%", "%killer% shot %player% to death", "%player% was shot by %killer%"};
	private static final String[] creeperMessages = new String[] {"Hello mine turtle! HELLO %player%! BOOM!", "%player% got blown to pieces"};
	private static final String[] zombieMessages = new String[] {"%player% decided to join the realm of the zombies", "%player% got eaten alive by a zombie"};
	private static final String[] voidMessages = new String[] {"%player% wanted to know what the bottom of the world looks like", "%player% lost their balance and fell into the void"};
	private static final String[] voidByPlayerMessages = new String[] {"%killer% ruthlessly pushed %player% into the void"};
	private static final String[] lavaMessages = new String[] {"%player% wanted to know way lava felt like", "No, %player% lava isn't baked beans!"};
	private static final String[] drownMessages = new String[] {"%player% ran out of breath whilst swimming", "%player% drowned", "%player% was too slow to get out of the water"};
	private static final String[] fallMessages = new String[] {"%player%'s body got crushed whilst falling", "%player% jumped from a little too high", "%player% is now as flat as a pancake because they fell too far"};
	private static final String[] fireMessages = new String[] {"%player% was burned alive", "%player% burned to death", "%player% was torched and sadly died", "%player% got roasted to death"};
	private static final String[] poisonMessages = new String[] {"%player% was sadly poisoned", "Some evil person decided to poison %player% :'("};
	private static final String[] starvationMessages = new String[] {"%player% was too poor to buy food and died", "%player% starved to death", "%player% didn't eat enough"};

	public Component getKillArrow(Component player, Component killer) {
		return replaceVars(random(arrowMessages), player, killer);
	}

	public Component getKillVoidByPlayer(Component player, Component killer) {
		return replaceVars(random(voidByPlayerMessages), player, killer);
	}
	public Component getPlayerKill(Component player, Component killer) {
		return replaceVars(random(playerMessages), player, killer);
	}

	public Component getKillCreeper(Component player) {
		return replaceVars(random(creeperMessages), player);
	}
	public Component getKillZombie(Component player) {
		return replaceVars(random(zombieMessages), player);
	}
	public Component getKillVoid(Component player) {
		return replaceVars(random(voidMessages), player);
	}
	public Component getKillLava(Component player) {
		return replaceVars(random(lavaMessages), player);
	}
	public Component getKillDrown(Component player) {
		return replaceVars(random(drownMessages), player);
	}
	public Component getKillFall(Component player) {
		return replaceVars(random(fallMessages), player);
	}
	public Component getKillFire(Component player) {
		return replaceVars(random(fireMessages), player);
	}
	public Component getKillPoison(Component player) {
		return replaceVars(random(poisonMessages), player);
	}
	public Component getKillStarvation(Component player) {
		return replaceVars(random(starvationMessages), player);
	}

	@EventHandler
	public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.VOID && e.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent cause) {
			this.lastDamager.put(e.getEntity().getName(), cause.getDamager());
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		EntityDamageEvent lastDamageEvent = player.getLastDamageCause();
		if (lastDamageEvent instanceof EntityDamageByEntityEvent damageEvent) {
			if (damageEvent.getDamager() instanceof Creeper) {
				event.deathMessage(getKillCreeper(player.displayName()));
			} else if (damageEvent.getDamager() instanceof Arrow arrow) {
				if (arrow.getShooter() instanceof LivingEntity livingEntity) {
					if (livingEntity.getType() == EntityType.PLAYER) {
						Player killerp = (Player)livingEntity;
						event.deathMessage(getKillArrow(player.displayName(), killerp.displayName()));
					} else {
						event.deathMessage(getKillArrow(player.displayName(), Component.text(livingEntity.getType().toString().toLowerCase())));
					}
				}
			} else if (damageEvent.getDamager() instanceof Player killerPlayer) {
				event.deathMessage(getPlayerKill(player.displayName(), killerPlayer.displayName()));
				if (EasyCraft.getCommandManager().isDropPlayerHeadOnKillEnabled()) {
					player.getWorld().dropItem(player.getLocation(), HeadUtils.getHead(player));
				}
			} else if (damageEvent.getDamager() instanceof Zombie) {
				event.deathMessage(getKillZombie(player.displayName()));
			}
		} else if (lastDamageEvent instanceof EntityDamageByBlockEvent) {
			EntityDamageByBlockEvent cause = (EntityDamageByBlockEvent) event.getEntity().getLastDamageCause();
			if (cause == null) {
				return;
			}

			if (cause.getCause() == EntityDamageEvent.DamageCause.VOID) {
				Entity entity = lastDamager.get(player.getName());
				lastDamager.remove(player.getName());
				if (entity != null) {
					if (entity instanceof Player killerPlayer) {
						event.deathMessage(getKillVoidByPlayer(player.displayName(), killerPlayer.displayName()));
					} else {
						event.deathMessage(getKillVoid(player.displayName()));
					}
				} else {
					event.deathMessage(getKillVoid(player.displayName()));
				}
			} else if (cause.getCause() == EntityDamageEvent.DamageCause.LAVA) {
				event.deathMessage(getKillLava(player.displayName()));
			} else if (cause.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
				event.deathMessage(getKillDrown(player.displayName()));
			} else if (cause.getCause() == EntityDamageEvent.DamageCause.FALL) {
				event.deathMessage(getKillFall(player.displayName()));
			} else if (cause.getCause() == EntityDamageEvent.DamageCause.FIRE) {
				event.deathMessage(getKillFire(player.displayName()));
			} else if (cause.getCause() == EntityDamageEvent.DamageCause.POISON) {
				event.deathMessage(getKillPoison(player.displayName()));
			} else if (cause.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
				event.deathMessage(getKillStarvation(player.displayName()));
			}
		}
	}

	private static Component replaceVars(String message, Component player, Component killer) {
		message = StringUtils.componentToString(replaceVars(message, player));
		if (message.contains("%killer%")) {
			message = message.replace("%killer%", StringUtils.componentToString(killer));
		}

		return Component.text(message);
	}

	private static Component replaceVars(String message, Component player) {
		if (message.contains("%player%")) {
			message = message.replace("%player%", StringUtils.componentToString(player));
		}

		return Component.text(message);
	}

	private static String random(String[] array) {
		int rnd = random.nextInt(array.length);
		return array[rnd];
	}
}
