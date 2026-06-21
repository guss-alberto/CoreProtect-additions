package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

import com.alisaa.coreprotectadditions.AdditionsConfigHandler;
import com.alisaa.coreprotectadditions.ApiWrapper;

public class EntityItemLogger implements Listener {
    private ApiWrapper api;

    public EntityItemLogger (ApiWrapper api){
        this.api = api;
    }

    private boolean shouldLogItem(EntityType type){
        switch (type) {
            case PLAYER:
                return false;
            case PIGLIN:
                return AdditionsConfigHandler.LOG_PIGLIN_ITEM;
            case ALLAY:
                return AdditionsConfigHandler.LOG_ALLAY_ITEM;
            case VILLAGER:
                return AdditionsConfigHandler.LOG_VILLAGER_ITEM;
            default:
                return AdditionsConfigHandler.LOG_OTHER_ENTITY_ITEM;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent e){
        if (shouldLogItem(e.getEntityType())){
            api.logItemPickup(e.getEntity(), e.getItem().getLocation(), e.getItem().getItemStack());
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onItemDrop(EntityDropItemEvent e){
        if (shouldLogItem(e.getEntityType())){
            api.logItemDrop(e.getEntity(), e.getItemDrop().getLocation(), e.getItemDrop().getItemStack());
        }
    }
}
