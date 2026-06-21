package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.potion.PotionEffectType;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.AdditionsConfigHandler;

public class EntityChangeBlockLogger implements Listener {

    private ApiWrapper api;

    public EntityChangeBlockLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockChange(EntityChangeBlockEvent e) {
        Block block = e.getBlock();
        Location location = block.getLocation();
        if (AdditionsConfigHandler.LOG_WEAVING && e.getTo() == Material.COBWEB &&
                e.getEntity() instanceof LivingEntity le &&
                le.getPotionEffect(PotionEffectType.WEAVING) != null) {
            api.logPlacement("#weaving", location, Material.COBWEB, null);
            return;
        }
        if (AdditionsConfigHandler.LOG_ZOMBIE_DOOR_BREAK && e instanceof EntityBreakDoorEvent) {
            api.logRemoval(e.getEntity(), location, block.getType());
            return;
        }
        if (AdditionsConfigHandler.LOG_SILVERFISH_INFESTATION && e.getEntityType() == EntityType.SILVERFISH
                && !e.getTo().isAir()) { // coreprotect already logs silverfish exiting
            api.logRemoval("#silverfish", block.getState());
            api.logPlacement("#silverfish", location, e.getTo(), null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockForm(EntityBlockFormEvent e) {
        if (AdditionsConfigHandler.LOG_FROST_WALKER && e.getNewState().getType() == Material.FROSTED_ICE) {
            String user = ApiWrapper.formatUser(e.getEntity());
            api.logRemoval(user, e.getBlock().getState());
            api.logPlacement(user, e.getNewState());
        }
    }
}
