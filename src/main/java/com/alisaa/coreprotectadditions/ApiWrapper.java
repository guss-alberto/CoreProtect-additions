package com.alisaa.coreprotectadditions;

import net.coreprotect.CoreProtectAPI;
import net.coreprotect.consumer.Queue;
import net.coreprotect.database.logger.ItemLogger;
import net.coreprotect.listener.player.ProjectileLaunchListener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ApiWrapper extends CoreProtectAPI {
    CoreProtectAPI api;

    public static String formatUser(Entity user) {
        if (user instanceof Player player) {
            return player.getName();
        }
        if (user != null){
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

    public boolean logItemThrow(String user, Location location, ItemStack item, boolean shootInsteadOfThrow){
        ProjectileLaunchListener.playerLaunchProjectile(location, user, item, 1, 1, 0,  (shootInsteadOfThrow ? ItemLogger.ITEM_SHOOT : ItemLogger.ITEM_THROW));
        return true;
    }
    

    private boolean isValidUserAndLocation(String user, Location location) {
        return user != null && location != null && !user.isEmpty();
    }
}
