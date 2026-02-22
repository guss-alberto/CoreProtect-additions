package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.ExplosionResult;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Candle;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.Switch;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.AbstractWindCharge;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import com.alisaa.coreprotectadditions.ApiWrapper;
import com.alisaa.coreprotectadditions.ConfigHandler;

public class WindChargeLogger implements Listener {
    private ApiWrapper api;

    public WindChargeLogger(ApiWrapper api) {
        this.api = api;
    }

    private boolean isAffectedByWindCharge(BlockData block) {
        // can't open iron doors/trapdoors
        if (block.getMaterial().name().startsWith("IRON_")) {
            return false;
        }

        if (block instanceof Gate) {
            return true;
        }
        if (block instanceof Switch) {
            return true;
        }
        if (block instanceof Candle candle) {
            // can only put out candles
            return candle.isLit();
        }
        if (block instanceof TrapDoor) {
            return true;
        }
        if (block instanceof Door door) {
            // don't wanna double-count the door
            return door.getHalf() == Bisected.Half.BOTTOM;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    public void onWindChargeExplosion(EntityExplodeEvent e) {
        if (!ConfigHandler.LOG_WIND_CHARGE_CLICK || e.getExplosionResult() != ExplosionResult.TRIGGER_BLOCK) {
            return;
        }

        if (e.getEntity() instanceof AbstractWindCharge windCharge) {
            String user = "#wind_charge";
            if (windCharge.getShooter() instanceof Entity entity) {
                user = ApiWrapper.formatUser(entity);
            }

            for (Block block : e.blockList()) {
                if (isAffectedByWindCharge(block.getBlockData())) {
                    api.logInteraction(user, block.getLocation());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onWindChargeLaunch(EntitySpawnEvent e) {
        if (!ConfigHandler.LOG_WIND_CHARGE_THROW){
            return;
        }
        if (e.getEntity() instanceof WindCharge windCharge && windCharge.getShooter() instanceof Player player) {
            api.logItemThrow(player.getName(), player.getLocation(), new ItemStack(Material.WIND_CHARGE, 1), false);
        }
    }

}
