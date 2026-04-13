package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.SizedFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.persistence.PersistentDataType;

import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.ConfigHandler;
import com.alisaa.coreprotectadditions.Main;

public class MobExplosionLogger implements Listener {
    private ApiWrapper api;
    public static final NamespacedKey targetKey = new NamespacedKey(Main.getInstance(), "target");
    public MobExplosionLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        Entity entity = e.getEntity();
        if (ConfigHandler.LOG_CREEPER && entity instanceof Creeper creeper) {
            Entity igniter = creeper.getIgniter();
            if (api.logInteraction(igniter, creeper.getLocation(), Material.CREEPER_SPAWN_EGG)) {
                return;
            }

            LivingEntity target = creeper.getTarget();
            api.logRemoval(target, creeper.getLocation(), Material.CREEPER_SPAWN_EGG);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (ConfigHandler.LOG_FIREBALL &&
                e.getEntity() instanceof SizedFireball fireball &&
                fireball.getShooter() instanceof Mob mob) {
            fireball.getPersistentDataContainer().set(
                    targetKey,
                    PersistentDataType.STRING,
                    ApiWrapper.formatUser(mob.getTarget()));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireballHit(ProjectileHitEvent e) {
        if (ConfigHandler.LOG_FIREBALL && e.getEntity() instanceof SizedFireball fireball) {
            String user = fireball.getPersistentDataContainer().get(targetKey, PersistentDataType.STRING);

            if (user == null || user.isEmpty()){
                user = ApiWrapper.formatUser(fireball);
            }
            api.logRemoval(user, fireball.getLocation(), Material.FIRE_CHARGE, null);
            
        }
    }
}