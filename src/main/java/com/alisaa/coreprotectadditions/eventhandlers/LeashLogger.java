package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

import com.alisaa.coreprotectadditions.ApiWrapper;

import io.papermc.paper.entity.Leashable;

public class LeashLogger implements Listener {
    private ApiWrapper api;

    public LeashLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerUnleashedEntityEvent(EntityUnleashEvent e) {
        Leashable entity = (Leashable) e.getEntity();

        if (e instanceof PlayerUnleashEntityEvent ep) {
            api.logRemoval(ep.getPlayer(), entity.getLocation(), Material.LEAD);
            return;
        }

        // If a player was holding the leash, log as player
        Entity leashHolder = entity.getLeashHolder();
        if (leashHolder instanceof Player) {
            api.logRemoval(leashHolder, entity.getLocation(), Material.LEAD);
            return;
        }

        // Check who was riding the entity and log as player
        if (!entity.isEmpty() && entity.getPassengers().getFirst() instanceof Player passenger) {
            api.logRemoval(passenger, entity.getLocation(), Material.LEAD);
            return;
        }

        // ignore this, will be logged on onLeashHitchBreak
        if (e.getReason().equals(EntityUnleashEvent.UnleashReason.HOLDER_GONE) && leashHolder instanceof LeashHitch) {
            return;
        }

        // if all else fails log as entity breaking own leash
        api.logRemoval(entity, entity.getLocation(), Material.LEAD);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeashHitchBreak(HangingBreakEvent e) {
        if (!(e.getEntity() instanceof LeashHitch)) {
            return;
        }

        Location pos = e.getEntity().getLocation();

        if (e instanceof HangingBreakByEntityEvent eb) {
            api.logRemoval(eb.getRemover(), pos, Material.LEAD);
            return;
        }

        api.logRemoval("#" + e.getCause().toString().toLowerCase(), pos, Material.LEAD, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent e) {
        Leashable entity = (Leashable) e.getEntity();
        // log the breaking of the old leash if possible
        if (entity.isLeashed()){
            api.logRemoval(e.getPlayer().getName(), entity.getLeashHolder().getLocation(), Material.LEAD, null);
        }

        // log putting a leash on the entity
        api.logPlacement(e.getPlayer().getName(), entity.getLocation(), Material.LEAD, null);
    }
}
