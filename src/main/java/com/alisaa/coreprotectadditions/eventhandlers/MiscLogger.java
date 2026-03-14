package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.ConfigHandler;

import io.papermc.paper.event.entity.EntityDyeEvent;
import io.papermc.paper.event.player.PlayerNameEntityEvent;

public class MiscLogger implements Listener {
    private ApiWrapper api;

    public MiscLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerNameEntityEvent(PlayerNameEntityEvent e) {
        if (ConfigHandler.LOG_ENTITY_RENAME) {
            api.logPlacement(e.getPlayer().getName(), e.getEntity().getLocation(), Material.NAME_TAG, null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRaidTrigger(RaidTriggerEvent e) {
        if (ConfigHandler.LOG_RAIDS) {
            api.logRemoval(e.getPlayer().getName(), e.getRaid().getLocation(), Material.OMINOUS_BOTTLE, null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onZombieBreakDoor(EntityBreakDoorEvent e) {
        if (ConfigHandler.LOG_ZOMBIE_DOOR_BREAK) {
            api.logRemoval(e.getEntity(), e.getBlock().getLocation(), e.getBlock().getType());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDye(EntityDyeEvent e) {
        if (!ConfigHandler.LOG_ENTITY_DYE) {
            return;
        }

        Player player = e.getPlayer();
        if (player == null) {
            return;
        }

        // TODO: figure out how to actually do this
        Material dye = Material.getMaterial(e.getColor().name() + "_DYE");
        api.logPlacement(player.getName(), e.getEntity().getLocation(), dye, null);
    }
}
