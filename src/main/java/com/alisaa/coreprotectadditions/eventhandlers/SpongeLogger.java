package com.alisaa.coreprotectadditions.eventhandlers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SpongeAbsorbEvent;

import com.alisaa.coreprotectadditions.ApiWrapper;

public class SpongeLogger implements Listener {
    ApiWrapper api;
    Block prevSponge;
    long prevTime = 0;

    public SpongeLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpongeAbsorbe(SpongeAbsorbEvent e) {

        // apparently the event gets called twice for some reason but only sometimes, so
        // i have to check that it's not the same block at the same time like this, it's
        // awful but there doesn't seem to be any better way
        Block sponge = e.getBlock();
        long time = sponge.getWorld().getTime();

        if (time == this.prevTime && sponge.equals(this.prevSponge)) {
            return;
        }
        this.prevTime = time;
        this.prevSponge = sponge;

        for (BlockState block : e.getBlocks()) {
            Location location = block.getLocation();
            // because of course the event gives the blockState for the blocks that will be
            // placed instead of the ones it replaced...
            Material type = location.getBlock().getType();

            if (type.equals(Material.WATER) || block.getBlockData() instanceof Waterlogged) {
                api.logRemoval("#sponge", location, Material.WATER, null);
            } else {
                // log kelp and seagreass as beging broken correctly
                api.logRemoval("#sponge", location, type, null);
            }
        }
    }

}
