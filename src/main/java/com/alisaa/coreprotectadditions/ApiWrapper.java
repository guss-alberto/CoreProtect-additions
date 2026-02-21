package com.alisaa.coreprotectadditions;

import java.lang.reflect.Method;

import net.coreprotect.CoreProtectAPI;
import net.coreprotect.consumer.Queue;
import net.coreprotect.listener.player.InventoryChangeListener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ApiWrapper extends CoreProtectAPI {
    CoreProtectAPI api;
    private Method onInventoryInteract = null;

    public ApiWrapper() {
        if (ConfigHandler.EXPERIMENTAL_ENTITY_CONTAINER_LOGGER) {
            try {
                onInventoryInteract = (InventoryChangeListener.class).getDeclaredMethod("onInventoryInteract",
                        String.class, Inventory.class,
                        ItemStack[].class, Material.class, Location.class, boolean.class);
                onInventoryInteract.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().severe("Unable to find onInventoryInteract method!");
                Bukkit.getLogger().severe("Disabling EXPERIMENTAL_ENTITY_CONTAINER_LOGGER");
                ConfigHandler.EXPERIMENTAL_ENTITY_CONTAINER_LOGGER = false;
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

    public boolean containerBreak(String user, Location location, Material type, ItemStack[] oldInventory) {
        if (this.isEnabled() && this.isValidUserAndLocation(user, location) && oldInventory != null) {
            Queue.queueContainerBreak(user, location, type, oldInventory);
            return true;
        }
        return false;
    }

    public boolean containerBreak(Entity entity, Location location, Material type, ItemStack[] oldInventory) {
        return containerBreak(formatUser(entity), location, type, oldInventory);
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

    private boolean isValidUserAndLocation(String user, Location location) {
        return user != null && location != null && !user.isEmpty();
    }
}
