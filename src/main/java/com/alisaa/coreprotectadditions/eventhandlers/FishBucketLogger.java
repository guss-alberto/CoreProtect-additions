package com.alisaa.coreprotectadditions.eventhandlers;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;

import com.alisaa.coreprotectadditions.ApiWrapper;


public class FishBucketLogger implements Listener {
    private ApiWrapper api;
    protected static final HashSet<Material> NON_FISH_BUCKETS = new HashSet<>(
        Arrays.asList(Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.POWDER_SNOW_BUCKET)
    );

    public FishBucketLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFish(PlayerBucketEntityEvent e){
        api.logRemoval(e.getPlayer(), e.getEntity().getLocation(),  e.getEntityBucket().getType());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerPlaceBucket(PlayerBucketEmptyEvent e){
        Material bucket = e.getBucket();
        if (!NON_FISH_BUCKETS.contains(bucket)){
            api.logPlacement(e.getPlayer(), e.getBlock().getLocation(), bucket);
        }
    }

}
