package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.alisaa.coreprotectadditions.ApiWrapper;

import io.papermc.paper.event.player.PlayerToggleEntityAgeLockEvent;

public class GoldenDandelionLogger implements Listener {
    private ApiWrapper api;

    public GoldenDandelionLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onAgeLock(PlayerToggleEntityAgeLockEvent e){
        api.logInteraction(e.getPlayer().getName(), e.getEntity().getLocation(), Material.GOLDEN_DANDELION);
    }

}
