package com.github.imdabigboss.easycraft.perks;

import com.github.imdabigboss.easycraft.EasyCraft;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EntityInabukketPerk extends RavelPerk implements Listener {
    public static final String NAME = "entityinabukket";
    public static final int CUSTOM_MODEL_DATA = 641238;

    private final EasyCraft plugin = EasyCraft.getInstance();
    private final NamespacedKey indabukketKey = new NamespacedKey(plugin, "indabukket");
    private final List<EntityType> allowedEntities = new ArrayList<>();

    public EntityInabukketPerk() {
        super(NAME, 3, false);

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        allowedEntities.add(EntityType.PIG);
        allowedEntities.add(EntityType.COW);
        allowedEntities.add(EntityType.SHEEP);
        allowedEntities.add(EntityType.CHICKEN);
        allowedEntities.add(EntityType.RABBIT);
        allowedEntities.add(EntityType.VILLAGER);
        allowedEntities.add(EntityType.FOX);
        allowedEntities.add(EntityType.HORSE);
        allowedEntities.add(EntityType.DONKEY);
        allowedEntities.add(EntityType.MULE);
        allowedEntities.add(EntityType.LLAMA);
    }

    @Override
    public boolean getPerk(Player player, String[] args) {
        if (args.length != 0) {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return false;
        }

        ItemStack item = new ItemStack(Material.STICK);

        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(ChatColor.AQUA + "Entity Inabukket"));

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.GRAY + "Shift right-click an"));
        lore.add(Component.text(ChatColor.GRAY + "entity to put it"));
        lore.add(Component.text(ChatColor.GRAY + "indabukket."));
        lore.add(Component.text(""));
        lore.add(Component.text(ChatColor.GRAY + "Right-click to take it"));
        lore.add(Component.text(ChatColor.GRAY + "outofdabukket."));
        meta.lore(lore);

        meta.setCustomModelData(CUSTOM_MODEL_DATA);
        item.setItemMeta(meta);

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You have no space in your inventory!");
            return false;
        } else {
            player.getInventory().addItem(item);
        }

        return true;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (player.isSneaking()) {
            return;
        }

        if (itemStack != null && itemStack.getType() == Material.STICK && itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData() && itemStack.getItemMeta().getCustomModelData() == CUSTOM_MODEL_DATA) {
            Location location = event.getInteractionPoint();

            if (location.getBlock().getType() != Material.AIR) {
                player.sendMessage(ChatColor.RED + "You can't put the entity there!");
                return;
            }

            this.handleEvent(itemStack, player, null, location, false);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (!player.isSneaking()) {
            return;
        }

        if (itemStack.getType() == Material.STICK && itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData() && itemStack.getItemMeta().getCustomModelData() == CUSTOM_MODEL_DATA) {
            this.handleEvent(itemStack, player, event.getRightClicked(), event.getRightClicked().getLocation(), true);
        }
    }

    private void handleEvent(ItemStack itemStack, Player player, Entity entity, Location location, boolean pickup) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.getPersistentDataContainer().has(this.indabukketKey)) {
            if (pickup) {
                player.sendMessage(ChatColor.RED + "There is already an entity inabukket!");
            } else {
                String entityString = meta.getPersistentDataContainer().get(this.indabukketKey, PersistentDataType.STRING);
                if (entityString != null) {
                    UUID entityUUID = UUID.fromString(entityString);
                    entity = this.plugin.getServer().getEntity(entityUUID);
                }

                if (entity != null) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.setFallDistance(0);
                    livingEntity.teleport(location);
                    livingEntity.setGravity(true);
                    livingEntity.setCollidable(true);
                    livingEntity.setPersistent(false);
                    livingEntity.setRemoveWhenFarAway(true);
                    livingEntity.setSilent(false);

                    livingEntity.setInvisible(false);
                    livingEntity.setAI(true);

                    player.sendMessage(ChatColor.AQUA + "You took the entity outofdabukket!");
                } else {
                    player.sendMessage(ChatColor.RED + "Something went wrong and the entity is lost.");
                }

                meta.getPersistentDataContainer().remove(this.indabukketKey);
                itemStack.setItemMeta(meta);

            }

            return;
        }

        if (entity == null) {
            player.sendMessage(ChatColor.RED + "There is no entity indabukket!");
            return;
        }

        if (!this.allowedEntities.contains(entity.getType())) {
            player.sendMessage(ChatColor.RED + "You can't pick up that entity!");
            return;
        }

        if (!pickup) {
            return;
        }

        meta.getPersistentDataContainer().set(this.indabukketKey, PersistentDataType.STRING, entity.getUniqueId().toString());
        itemStack.setItemMeta(meta);

        LivingEntity livingEntity = (LivingEntity) entity;
        livingEntity.setAI(false);
        livingEntity.setInvisible(true);

        livingEntity.setGravity(false);
        livingEntity.setCollidable(false);
        livingEntity.setPersistent(true);
        livingEntity.setRemoveWhenFarAway(false);
        livingEntity.setSilent(true);

        livingEntity.teleport(new Location(player.getWorld(), 0, player.getWorld().getMinHeight() - 2, 0));

        player.sendMessage(ChatColor.AQUA + "You put the entity inabukket!");
    }

    @Override
    public boolean isUndroppable(ItemStack item) {
        return item.getType() == Material.STICK &&
                item.hasItemMeta() &&
                item.getItemMeta().hasCustomModelData() &&
                item.getItemMeta().getCustomModelData() == CUSTOM_MODEL_DATA;
    }
}
