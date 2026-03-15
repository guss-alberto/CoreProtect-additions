package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.potion.PotionEffectType;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.ConfigHandler;

public class EntityChangeBlockLogger implements Listener {

    private ApiWrapper api;

    public EntityChangeBlockLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockChange(EntityChangeBlockEvent e) {
        if (ConfigHandler.LOG_WEAVING && e.getTo() == Material.COBWEB &&
                e.getEntity() instanceof LivingEntity le &&
                le.getPotionEffect(PotionEffectType.WEAVING) != null) {
            api.logPlacement("#weaving", e.getBlock().getLocation(), Material.COBWEB, null);
            return;
        }
        if (ConfigHandler.LOG_ZOMBIE_DOOR_BREAK && e instanceof EntityBreakDoorEvent) {
            api.logRemoval(e.getEntity(), e.getBlock().getLocation(), e.getBlock().getType());
            return;
        }
        if (ConfigHandler.LOG_SILVERFISH_INFESTATION && e.getEntityType() == EntityType.SILVERFISH
                && !Tag.AIR.getValues().contains(e.getTo())) { // coreprotect already logs silverfish exiting
            api.logRemoval("#silverfish", e.getBlock().getState());
            api.logPlacement("#silverfish", e.getBlock().getLocation(), e.getTo(), null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockForm(EntityBlockFormEvent e) {
        if (ConfigHandler.LOG_FROST_WALKER && e.getNewState().getType() == Material.FROSTED_ICE) {
            String user = ApiWrapper.formatUser(e.getEntity());
            api.logRemoval(user, e.getBlock().getState());
            api.logPlacement(user, e.getNewState());
        }
    }
}
