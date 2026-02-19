package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.ConfigHandler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.TNTPrimeEvent;
import org.bukkit.event.block.TNTPrimeEvent.*;
import org.bukkit.event.entity.EntityDamageEvent;

public class TntLogger implements Listener {
    private ApiWrapper api;

    public TntLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onIgniteTNT(TNTPrimeEvent e) {
        Location location = e.getBlock().getLocation();
        Entity entity = e.getPrimingEntity();

        if (entity instanceof Player player) {
            api.logInteraction(player.getName(), location);
            return;
        }

        PrimeCause cause = e.getCause();
        switch (cause) {
            case REDSTONE:
                if (ConfigHandler.LOG_REDSTONE_TNT_IGNITE){
                    api.logInteraction("#redstone", location);
                }
                break;
            case DISPENSER, FIRE:
                api.logInteraction("#" + cause.toString().toLowerCase(), location);
                break;

            case PROJECTILE:
                if (entity instanceof Projectile projectile && 
                    projectile.getShooter() instanceof Entity shooter){
                        api.logInteraction(shooter, location);
                }
                // fallback, in case no shooter exists, simply log the projectile
                api.logInteraction(entity, location);
                break;

            case EXPLOSION:
                if (e.getPrimingBlock() != null) {
                    Material material = e.getPrimingBlock().getType();
                    // most block explosions are just logged as air, since the block
                    // is broken by the explosion, but i'll leave this
                    if (!material.isAir()) {
                        api.logInteraction("#" + material.name().toLowerCase(), location);
                    } else {
                        api.logInteraction("#block", location);
                    }
                    break;
                }
                // if tnt chain, log as tnt cause
                if (entity instanceof TNTPrimed tnt) {
                    Entity igniterEntity = tnt.getSource();
                    if (igniterEntity == null) {
                        break;
                    }
                    api.logInteraction(igniterEntity, location);
                    break;
                }
                // find last damage if from ender crystal
                if (entity instanceof EnderCrystal enderCrystal) {
                    EntityDamageEvent damageEvent = enderCrystal.getLastDamageCause();
                    Entity damager = damageEvent.getDamageSource().getCausingEntity();
                    if (damager == null) {
                        api.logInteraction("#end_crystal", location);
                        break;
                    }
                    api.logInteraction(damager, location);
                    entity = damager;
                    break;
                }
                // Otherisw simply log the entity name
                api.logInteraction(entity, location);
                break;
            default:
                break;
        }
    }
}
