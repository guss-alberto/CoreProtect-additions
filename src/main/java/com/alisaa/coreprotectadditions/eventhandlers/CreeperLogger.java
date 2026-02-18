package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.SizedFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.alisaa.coreprotectadditions.ApiWrapper;

public class CreeperLogger implements Listener {
    private ApiWrapper api;

    public CreeperLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof Creeper creeper) {
            Entity igniter = creeper.getIgniter();
            if (api.logInteraction(igniter, creeper.getLocation(), Material.CREEPER_SPAWN_EGG)) {
                return;
            }

            LivingEntity target = creeper.getTarget();
            api.logRemoval(target, creeper.getLocation(), Material.CREEPER_SPAWN_EGG);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireballHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof SizedFireball fireball) {
            ProjectileSource shooter = fireball.getShooter();
            if (shooter instanceof Mob mob) {
                LivingEntity target = mob.getTarget();
                api.logRemoval(target, fireball.getLocation(), Material.FIRE_CHARGE);
            } else {
                api.logRemoval("#fireball", fireball.getLocation(), Material.FIRE_CHARGE, null);
            }
        }

    }
}