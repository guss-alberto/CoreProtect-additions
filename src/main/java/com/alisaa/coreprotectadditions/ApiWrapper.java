package com.alisaa.coreprotectadditions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.coreprotect.CoreProtectAPI;
import net.coreprotect.config.ConfigHandler;
import net.coreprotect.consumer.Queue;
import net.coreprotect.database.logger.ItemLogger;
import net.coreprotect.listener.player.ProjectileLaunchListener;
import net.coreprotect.listener.entity.EntityDeathListener;
import net.coreprotect.listener.player.InventoryChangeListener;
import net.coreprotect.listener.player.PlayerDropItemListener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ApiWrapper extends CoreProtectAPI {
    CoreProtectAPI api;
    private Method onInventoryInteract = null;
    public static final EntityDeathListener entityDeathListener = new EntityDeathListener();

    public ApiWrapper() {
        if (AdditionsConfigHandler.EXPERIMENTAL_ENTITY_CONTAINER_LOGGER) {
            try {
                onInventoryInteract = (InventoryChangeListener.class).getDeclaredMethod("onInventoryInteract",
                        String.class, Inventory.class,
                        ItemStack[].class, Material.class, Location.class, boolean.class);
                onInventoryInteract.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Unable to find onInventoryInteract method!");
                Bukkit.getLogger().severe("Disabling EXPERIMENTAL_ENTITY_CONTAINER_LOGGER");
                AdditionsConfigHandler.EXPERIMENTAL_ENTITY_CONTAINER_LOGGER = false;
                onInventoryInteract = null;
            }
        }
    }

    public static String formatUser(Entity user) {
        if (user instanceof Player player) {
            return player.getName();
        }
        if (user != null) {
            return "#" + user.getName().toLowerCase().replace(" ", "_");
        }
        return null;
    }

    public boolean logInteraction(String user, Location location, Material material) {
        if (this.isEnabled() && this.isValidUserAndLocation(user, location)) {
            Queue.queuePlayerInteraction(user, location.getBlock().getState(), material);
            return true;
        }
        return false;
    }

    public boolean logInteraction(Entity entity, Location location, Material material) {
        String user = formatUser(entity);
        return logInteraction(user, location, material);
    }

    public boolean logInteraction(Entity entity, Location location) {
        String user = formatUser(entity);
        return logInteraction(user, location);
    }

    public boolean logPlacement(Entity entity, Location location, Material material) {
        String user = formatUser(entity);
        return logPlacement(user, location, material, null);
    }

    public boolean logRemoval(Entity entity, Location location, Material material) {
        String user = formatUser(entity);
        return logRemoval(user, location, material, null);
    }

    public boolean logItemThrow(String user, Location location, ItemStack item, boolean shootInsteadOfThrow) {
        ProjectileLaunchListener.playerLaunchProjectile(location, user, item, 1, 1, 0,
                (shootInsteadOfThrow ? ItemLogger.ITEM_SHOOT : ItemLogger.ITEM_THROW));
        return true;
    }

    public String getTransactingChestId(Location location) {
        return location.getWorld().getUID().toString() + "." + location.getBlockX() + "." + location.getBlockY() + "."
                + location.getBlockZ();
    }

    public String getLoggingChestId(String user, Location location) {
        return user.toLowerCase() + "." + location.getBlockX() + "." + location.getBlockY() + "."
                + location.getBlockZ();
    }

    public boolean inventoryTransaction(String user, InventoryHolder holder, Location location) {

        if (isValidUserAndLocation(user, location) && holder != null && onInventoryInteract != null) {
            Inventory inventory = holder.getInventory();
            try {
                return (boolean) onInventoryInteract.invoke(null, user, inventory, inventory.getContents(), null,
                        location, true);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Exception happened while handling invenoty transaction!");
                Bukkit.getLogger().severe("Disabling EXPERIMENTAL_ENTITY_CONTAINER_LOGGER");
                onInventoryInteract = null;
            }

        }
        return false;
    }

    public void logEntityKill(LivingEntity entity, DamageSource damage) {
        // CoreProtect uses the last damage cuase so it needs to be damaged with a
        // damage of zero for it to be logged correctly
        entity.damage(0, damage);
        // The function that takes the entity directly is private, so we use call the
        // eventHandler directly instead
        EntityDeathEvent fakeEvent = new EntityDeathEvent(entity, damage, new ArrayList<>());
        entityDeathListener.onEntityDeath(fakeEvent);
        // shouldn't be necessary, but cancel it just to be sure
        fakeEvent.setCancelled(true);
    }

    public void logEntityKill(LivingEntity entity, Entity killer) {
        DamageSource damage = DamageSource.builder(DamageType.GENERIC)
                .withDirectEntity(killer)
                .withCausingEntity(killer)
                .build();
        logEntityKill(entity, damage);
    }

    public void logEntityKill(LivingEntity entity, Entity killer, DamageType damageType) {
        DamageSource damage = DamageSource.builder(damageType)
                .withDirectEntity(killer)
                .withCausingEntity(killer)
                .build();
        logEntityKill(entity, damage);
    }

    // Warning, will not work with all damageTypes, you should add the entity
    // whenever possible
    public void logEntityKill(LivingEntity entity, DamageType damageType) {
        DamageSource damage = DamageSource.builder(damageType).build();
        logEntityKill(entity, damage);
    }

    // CoreProtect's built-in only supports Players so I re-made it 
    public boolean logItemPickup(String user, Location location, ItemStack item) {
        if (this.isEnabled() && location != null && user != null && item != null) {
            String loggingItemId = user.toLowerCase() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
            int itemId = getItemId(loggingItemId);
            List<ItemStack> list = ConfigHandler.itemsPickup.getOrDefault(loggingItemId, new ArrayList<>());
            list.add(item.clone());
            ConfigHandler.itemsPickup.put(loggingItemId, list);
            int time = (int) (System.currentTimeMillis() / 1000L) + 1;
            Queue.queueItemTransaction(user, location.clone(), time, 0, itemId);
            return true;
        }
        return false;
    }

    public boolean logItemPickup(Entity user, Location location, ItemStack item) {
        return logItemPickup(formatUser(user), location, item);
    }

    public boolean logItemDrop(String user, Location location, ItemStack item) {
        if (this.isEnabled() && location != null && user != null) {
            PlayerDropItemListener.playerDropItem(location, user, item);
            return true;
        }
        return false;
    }

    public boolean logItemDrop(Entity user, Location location, ItemStack item) {
        return logItemDrop(formatUser(user), location, item);
    }

    private boolean isValidUserAndLocation(String user, Location location) {
        return user != null && location != null && !user.isEmpty();
    }
}
