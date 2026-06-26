package com.alisaa.coreprotectadditions.eventhandlers;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.alisaa.coreprotectadditions.AdditionsConfigHandler;
import com.alisaa.coreprotectadditions.ApiWrapper;

public class AllayLogger implements Listener {
    private ApiWrapper api;

    public AllayLogger(ApiWrapper api) {
        this.api = api;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractAllay(PlayerInteractAtEntityEvent e) {
        if (!(e.getRightClicked() instanceof Allay allay) || !AdditionsConfigHandler.LOG_ALLAY_SWITCH_ITEM) {
            return;
        }

        Player player = e.getPlayer();
        var hand = e.getHand();
        ItemStack interactionItem = player.getEquipment().getItem(hand);
        ItemStack allayItem = allay.getEquipment().getItemInMainHand();

        if (allay.isDancing()
                && allay.canDuplicate()
                && Tag.ITEMS_DUPLICATES_ALLAYS.isTagged(interactionItem.getType())) {
            // allay duplication
            return;
        }


        // Give item to allay
        if (allayItem.getType() == Material.AIR
                && interactionItem.getType() != Material.AIR) {
            api.logItemDrop(player.getName(), allay.getLocation(), interactionItem);
            api.logItemPickup("#allay", allay.getLocation(), interactionItem);
            return;
        }

        // Take item from allay
        if (allayItem.getType() != Material.AIR
                && hand == EquipmentSlot.HAND
                && interactionItem.getType() == Material.AIR) {
            api.logItemDrop("#allay", allay.getLocation(), allayItem);
            api.logItemPickup(player.getName(), allay.getLocation(), allayItem);
        }
    }

}
