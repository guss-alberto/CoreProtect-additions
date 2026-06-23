package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Allay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.raid.RaidTriggerEvent;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.AdditionsConfigHandler;
import com.destroystokyo.paper.event.entity.EntityZapEvent;

import io.papermc.paper.event.entity.EntityDyeEvent;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import io.papermc.paper.event.player.PlayerInsertLecternBookEvent;
import io.papermc.paper.event.player.PlayerNameEntityEvent;

public class MiscLogger implements Listener {
    private ApiWrapper api;

    public MiscLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerNameEntityEvent(PlayerNameEntityEvent e) {
        if (AdditionsConfigHandler.LOG_ENTITY_RENAME) {
            api.logPlacement(e.getPlayer().getName(), e.getEntity().getLocation(), Material.NAME_TAG, null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onRaidTrigger(RaidTriggerEvent e) {
        if (AdditionsConfigHandler.LOG_RAIDS) {
            api.logRemoval(e.getPlayer().getName(), e.getRaid().getLocation(), Material.OMINOUS_BOTTLE, null);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLightningConversion(EntityZapEvent e) {
        if (AdditionsConfigHandler.LOG_LIGHTNING_CONVERSION && e.getEntity() instanceof LivingEntity entity) {
            api.logEntityKill(entity, e.getBolt(), DamageType.LIGHTNING_BOLT);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity (PlayerInteractAtEntityEvent e){
        if ( e.getRightClicked() instanceof Allay allay ){
            api.logInteraction(e.getPlayer(), allay.getLocation(), Material.ALLAY_SPAWN_EGG);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFlowerPotManipulate(PlayerFlowerPotManipulateEvent e) {
        if (!AdditionsConfigHandler.LOG_FLOWER_POT){
            return;
        }
        api.logRemoval(e.getPlayer().getName(), e.getFlowerpot().getState());
        if (e.isPlacing()) {
            Material newPot = Material.getMaterial("POTTED_" + e.getItem().getType().name());
            api.logPlacement(e.getPlayer(), e.getFlowerpot().getLocation(), newPot);
        } else {
            api.logPlacement(e.getPlayer(), e.getFlowerpot().getLocation(), Material.FLOWER_POT);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAddBook(PlayerInsertLecternBookEvent e) {
        if (AdditionsConfigHandler.LOG_LECTERN_INSERT) {
            api.logContainerTransaction(e.getPlayer().getName(), e.getLectern().getLocation());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDye(EntityDyeEvent e) {
        if (!AdditionsConfigHandler.LOG_ENTITY_DYE) {
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
