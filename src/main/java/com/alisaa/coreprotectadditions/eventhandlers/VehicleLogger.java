package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Mob;
import org.bukkit.entity.minecart.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.ConfigHandler;

public class VehicleLogger implements Listener {
    private ApiWrapper api;

    public VehicleLogger(ApiWrapper api) {
        this.api = api;
    }

    private boolean shouldLogPlacement(Entity entity) {
        if (entity instanceof ExplosiveMinecart) {
            return true;
        }
        if (!ConfigHandler.LOG_CHEST_BOAT && entity instanceof ChestBoat) {
            return false;
        }

        if (ConfigHandler.LOG_BOAT && entity instanceof Boat) {
            return true;
        }

        if (!ConfigHandler.LOG_HOPPER_MINECART && entity instanceof HopperMinecart) {
            return false;
        }

        if (!ConfigHandler.LOG_CHEST_MINECART && entity instanceof StorageMinecart) {
            return false;
        }

        if (ConfigHandler.LOG_MINECART && entity instanceof Minecart) {
            return true;
        }

        return false;
    }

    private boolean shouldLogRiding(Entity entity) {
        if (ConfigHandler.LOG_INVENTORY_RIDE && entity instanceof InventoryHolder inventory &&
                !inventory.getInventory().isEmpty()) {
            return true;
        }
        if (ConfigHandler.LOG_MOB_RIDE && entity instanceof Mob) {
            return true;
        }
        if (ConfigHandler.LOG_MINECART_RIDE && entity instanceof Minecart) {
            return true;
        }
        if (ConfigHandler.LOG_BOAT_RIDE && entity instanceof Boat) {
            return true;
        }

        return false;
    }

    private boolean shouldLogClick(Entity entity) {
        if (!ConfigHandler.LOG_ENTITY_CONTAINER_CLICK) {
            return false;
        }

        if (entity instanceof StorageMinecart) {
            return true;
        }
        if (entity instanceof HopperMinecart) {
            return true;
        }
        if (entity instanceof ChestBoat) {
            return true;
        }
        if (entity instanceof ChestedHorse ch && ch.isCarryingChest()) {
            return true;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlacement(EntityPlaceEvent e) {
        Player player = e.getPlayer();
        if (player == null || !shouldLogPlacement(e.getEntity())) {
            return;
        }

        Material item = player.getInventory().getItem(e.getHand()).getType();
        Location location = e.getBlock().getLocation();

        api.logPlacement(player.getName(), location, item, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRemoval(VehicleDestroyEvent e) {
        Vehicle entity = e.getVehicle();
        if (!shouldLogPlacement(entity)) {
            return;
        }
        Entity attacker = e.getAttacker();
        Material item = entity.getPickItemStack().getType();

        if (api.logRemoval(attacker, entity.getLocation(), item)) {
            return;
        }

        api.logRemoval(entity, entity.getLocation(), item);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityMount(EntityMountEvent e) {
        Entity rider = e.getEntity();

        if (!ConfigHandler.LOG_NON_PLAYER_RIDE && !(rider instanceof Player)) {
            return;
        }

        Entity mount = e.getMount();

        if (!shouldLogRiding(mount)) {
            return;
        }

        Material type = mount.getPickItemStack().getType();
        if (!(mount instanceof Mob) && ConfigHandler.LOG_RIDE_AS_CLICK) {
            api.logInteraction(rider, mount.getLocation(), type);
        } else {
            api.logPlacement(rider, mount.getLocation(), type);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDismount(EntityDismountEvent e) {
        Entity rider = e.getEntity();

        if (!(rider instanceof Player)) {
            return;
        }

        Entity mount = e.getDismounted();

        if (!shouldLogRiding(mount)) {
            return;
        }

        Material type = mount.getPickItemStack().getType();
        if (!(mount instanceof Mob) && ConfigHandler.LOG_RIDE_AS_CLICK) {
            api.logInteraction(rider, mount.getLocation(), type);
        } else {
            api.logRemoval(rider, mount.getLocation(), type);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onGuiOpen(InventoryOpenEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Entity entity && shouldLogClick(entity)) {
            Material material = entity.getPickItemStack().getType();
            api.logInteraction(e.getPlayer(), entity.getLocation(), material);
        }
    }
}
