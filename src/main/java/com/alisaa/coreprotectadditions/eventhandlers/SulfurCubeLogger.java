package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SulfurCube;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.Main;

import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;

public class SulfurCubeLogger implements Listener {
    public static final int DEFAULT_SULFUR_CUBE_FUSE = 120;
    public static final int MAX_ATTRIBUTION_TICKS_AFTER_HIT = 200 + DEFAULT_SULFUR_CUBE_FUSE; // 10 s + 6 seconds of fuse

    public static final NamespacedKey igniterKey = new NamespacedKey(Main.getInstance(), "igniter");
    public static final NamespacedKey lastPlayerHitKey = new NamespacedKey(Main.getInstance(), "last_player_hit");
    public static final NamespacedKey lastHitTimeKey = new NamespacedKey(Main.getInstance(), "last_hit_time");
    //public static final NamespacedKey bucketOwnerKey = new NamespacedKey(Main.getInstance(), "bucket_owner");
    
    private final ApiWrapper api;

    public SulfurCubeLogger(ApiWrapper api) {
        this.api = api;
    }

    // Manual right-click ignition
    @EventHandler(ignoreCancelled = true)
    public void onPlayerIgnite(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof SulfurCube sCube)) return;

        ItemStack item = e.getPlayer().getInventory().getItem(e.getHand());
        if (item.getType() == Material.FLINT_AND_STEEL || item.getType() == Material.FIRE_CHARGE) {
            sCube.getPersistentDataContainer().set(igniterKey, PersistentDataType.STRING, e.getPlayer().getName());
        }

        // Track when a player gives the sulfur cube TNT the first time
        if (item.getType() == Material.TNT && !sCube.canExplode()){
            PersistentDataContainer pdc = sCube.getPersistentDataContainer();
            pdc.set(lastPlayerHitKey, PersistentDataType.STRING,  e.getPlayer().getName());
            pdc.set(lastHitTimeKey, PersistentDataType.INTEGER, Bukkit.getCurrentTick());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSulfurCubeHit(EntityPushedByEntityAttackEvent e) {
        // Only log if sulfur cube is not already ignited, or if it ignited this very tick
        if (e.getEntity() instanceof SulfurCube sCube && (sCube.getFuseTicks() == -1 || sCube.getFuseTicks() == DEFAULT_SULFUR_CUBE_FUSE)) {
            Entity attacker = e.getPushedBy();

            if (attacker instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooterEntity){
                attacker = shooterEntity;
            }

            PersistentDataContainer pdc = sCube.getPersistentDataContainer();
            pdc.set(lastPlayerHitKey, PersistentDataType.STRING, ApiWrapper.formatUser(attacker));
            pdc.set(lastHitTimeKey, PersistentDataType.INTEGER, Bukkit.getCurrentTick());
        }
    }

    /*@EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (e.getBucket() == Material.SULFUR_CUBE_BUCKET) {
            for (Entity entity : e.getBlock().getLocation().getNearbyLivingEntities(1)) {
                if (entity instanceof SulfurCube sCube) {
                    sCube.getPersistentDataContainer().set(bucketOwnerKey, PersistentDataType.STRING, ApiWrapper.formatUser(e.getPlayer()));
                }
            }
        }
    }*/

    @EventHandler(ignoreCancelled = true)
    public void onSulfurCubeIgnite(ExplosionPrimeEvent e) {
        if (e.getEntity() instanceof SulfurCube sCube) {
            PersistentDataContainer pdc = sCube.getPersistentDataContainer();

            if (pdc.has(igniterKey, PersistentDataType.STRING)) {
                String igniter = pdc.get(igniterKey, PersistentDataType.STRING);
                api.logInteraction(igniter, sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                return;
            }

            if (pdc.has(lastPlayerHitKey, PersistentDataType.STRING) && pdc.has(lastHitTimeKey, PersistentDataType.INTEGER)) {
                int lastHitTime = pdc.get(lastHitTimeKey, PersistentDataType.INTEGER);
                if (Bukkit.getCurrentTick() - lastHitTime < MAX_ATTRIBUTION_TICKS_AFTER_HIT) {
                    String lastHitPlayer = pdc.get(lastPlayerHitKey, PersistentDataType.STRING);
                    api.logInteraction(lastHitPlayer, sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                    return;
                }
            }

            if (sCube.isLeashed() && sCube.getLeashHolder() instanceof Player leashHolder) {
                api.logInteraction(leashHolder.getName(), sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                return;
            }

            // handle the happy ghast bomber thing
            if (sCube.isLeashed() && sCube.getLeashHolder() instanceof HappyGhast happyGhast && !happyGhast.getPassengers().isEmpty()) {
                api.logInteraction(ApiWrapper.formatUser(happyGhast.getPassengers().getFirst()), sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                return;
            }

            /*if (pdc.has(bucketOwnerKey, PersistentDataType.STRING)) {
                String owner = pdc.get(bucketOwnerKey, PersistentDataType.STRING);
                api.logInteraction(owner, sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                return;
            }*/

            api.logInteraction("#sulfur_cube", sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
        }
    }
}