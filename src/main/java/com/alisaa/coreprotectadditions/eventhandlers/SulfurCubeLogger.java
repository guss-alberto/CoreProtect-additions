package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SulfurCube;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.Main;

import io.papermc.paper.event.entity.EntityIgniteEvent;
import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;

public class SulfurCubeLogger implements Listener {
    public static final NamespacedKey igniterKey = new NamespacedKey(Main.getInstance(), "igniter");
    public static final NamespacedKey lastPlayerHitKey = new NamespacedKey(Main.getInstance(), "last_player_hit");
    public static final NamespacedKey lastHitTimeKey = new NamespacedKey(Main.getInstance(), "last_hit_time");
    public static final NamespacedKey bucketOwnerKey = new NamespacedKey(Main.getInstance(), "bucket_owner");
    
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
            sCube.getPersistentDataContainer().set(igniterKey, PersistentDataType.STRING, ApiWrapper.formatUser(e.getPlayer()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSulfurCubeHit(EntityPushedByEntityAttackEvent e) {
        if (e.getEntity() instanceof SulfurCube sCube) {
            Entity attacker = e.getPushedBy();

            PersistentDataContainer pdc = sCube.getPersistentDataContainer();
            pdc.set(lastPlayerHitKey, PersistentDataType.STRING, ApiWrapper.formatUser(attacker));
            pdc.set(lastHitTimeKey, PersistentDataType.LONG, System.currentTimeMillis());

            System.err.println(ApiWrapper.formatUser(attacker));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (e.getBucket() == Material.SULFUR_CUBE_BUCKET) {
            for (Entity entity : e.getBlock().getLocation().getNearbyLivingEntities(1)) {
                if (entity instanceof SulfurCube sCube) {
                    sCube.getPersistentDataContainer().set(bucketOwnerKey, PersistentDataType.STRING, ApiWrapper.formatUser(e.getPlayer()));
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSulfurCubeIgnite(EntityIgniteEvent e) {
        if (e.getEntity() instanceof SulfurCube sCube) {
            PersistentDataContainer pdc = sCube.getPersistentDataContainer();

            if (pdc.has(igniterKey, PersistentDataType.STRING)) {
                String igniter = pdc.get(igniterKey, PersistentDataType.STRING);
                api.logInteraction(igniter, sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                return;
            }


            if (pdc.has(lastPlayerHitKey, PersistentDataType.STRING) && pdc.has(lastHitTimeKey, PersistentDataType.LONG)) {
                long lastHitTime = pdc.get(lastHitTimeKey, PersistentDataType.LONG);
                if (System.currentTimeMillis() - lastHitTime < 1000) {
                    String lastHitPlayer = pdc.get(lastPlayerHitKey, PersistentDataType.STRING);
                    api.logInteraction(lastHitPlayer, sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                    return;
                }
            }

            if (sCube.isLeashed() && sCube.getLeashHolder() instanceof Player leashHolder) {
                api.logInteraction(leashHolder.getName(), sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                return;
            }

            if (pdc.has(bucketOwnerKey, PersistentDataType.STRING)) {
                String owner = pdc.get(bucketOwnerKey, PersistentDataType.STRING);
                api.logInteraction(owner, sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
                return;
            }

            api.logInteraction("#sulfur_cube", sCube.getLocation(), Material.SULFUR_CUBE_SPAWN_EGG);
        }
    }
}