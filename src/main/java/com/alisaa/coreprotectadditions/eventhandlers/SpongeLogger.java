package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SpongeAbsorbEvent;

import com.alisaa.coreprotectadditions.ApiWrapper;

public class SpongeLogger implements Listener {
    ApiWrapper api;

    public SpongeLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpongeAbsorbe(SpongeAbsorbEvent e) {
        for (BlockState block : e.getBlocks()) {
            api.logRemoval("#sponge", block.getLocation(), Material.WATER, null);
        }
    }

}
