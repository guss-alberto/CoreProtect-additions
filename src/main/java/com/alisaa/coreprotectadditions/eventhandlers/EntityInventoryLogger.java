package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

import com.alisaa.coreprotectadditions.ApiWrapper;

public class EntityInventoryLogger implements Listener {
    private ApiWrapper api;

    public EntityInventoryLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryAction inventoryAction = event.getAction();

        if (inventoryAction == InventoryAction.NOTHING) {
            return;
        }

        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        // ignore player using their own inventory
        if (inventoryHolder instanceof Entity entity && !(entity instanceof HumanEntity)) {
            Player player = (Player) event.getWhoClicked();
            api.inventoryTransaction(player.getName().toLowerCase(), inventoryHolder, entity.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDragEvent(InventoryDragEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (inventoryHolder instanceof Entity entity) {
            Player player = (Player) event.getWhoClicked();
            api.inventoryTransaction(player.getName().toLowerCase(), inventoryHolder, entity.getLocation());
        }
    }
}
