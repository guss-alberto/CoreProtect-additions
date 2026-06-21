package com.alisaa.coreprotectadditions.eventhandlers;

import net.coreprotect.CoreProtectAPI;

import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.alisaa.coreprotectadditions.AdditionsConfigHandler;

import io.papermc.paper.block.bed.*;

public class BedLogger implements Listener {
    private CoreProtectAPI api;

    public BedLogger(CoreProtectAPI api) {
        this.api = api;
    }

    // explosions and set spawn without sleeping count as cancelled event
    @EventHandler(ignoreCancelled = false)
    public void onBedInteract(PlayerBedEnterEvent e) {
        try {
            BedEnterAction enterAction = e.enterAction();

            // log spawn set or explosion trigger
            if (enterAction.problem() == BedEnterProblem.EXPLOSION ||
                    (AdditionsConfigHandler.LOG_SPAWN_SET && enterAction.canSetSpawn().success())) {
                api.logInteraction(e.getPlayer().getName(), e.getBed().getLocation());
            }
            // for versions before 1.21.11, BedEnterAction does not exist
        } catch (NoSuchMethodError error) {
            if (!(AdditionsConfigHandler.LOG_SPAWN_SET && e.getBed().getWorld().isBedWorks())){
                return;
            }
            api.logInteraction(e.getPlayer().getName(), e.getBed().getLocation());
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractRespawnAnchor(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // nothing happens when clicking the respawn anchor if unloaded
        if (e.getClickedBlock().getBlockData() instanceof RespawnAnchor respawnAnchor
                && respawnAnchor.getCharges() > 0) {
            if (!AdditionsConfigHandler.LOG_SPAWN_SET && e.getClickedBlock().getWorld().isRespawnAnchorWorks()) {
                return;
            }
            api.logInteraction(e.getPlayer().getName(), e.getClickedBlock().getLocation());
        }
    }
}
