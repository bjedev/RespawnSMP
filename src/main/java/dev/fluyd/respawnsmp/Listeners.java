package dev.fluyd.respawnsmp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class Listeners implements Listener {
    @EventHandler
    public void onBedPlace(BlockPlaceEvent e) {

    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        // Check if the result is RED_BED
        if (e.getRecipe().getResult().getType() == Material.RED_BED) {
            // Check if the player has the permission to place a bed
            if (e.getWhoClicked().hasPermission("respawnsmp.bed")) {
                // Check if the player has already placed a bed
                if (RespawnSMP.allowedPlacements.containsKey(e.getWhoClicked())) {
                    // Check if the player has already placed 1 bed
                    if (RespawnSMP.allowedPlacements.get(e.getWhoClicked()) == 1) {
                        // Cancel the event
                        e.setCancelled(true);
                        // Send the player a message
                        e.getWhoClicked().sendMessage(ChatColor.RED + "You have already placed a bed!");
                    } else {
                        // Add the player to the allowedPlacements map
                        RespawnSMP.allowedPlacements.put((Player) e.getWhoClicked(), 1);
                    }
                } else {
                    // Add the player to the allowedPlacements map
                    RespawnSMP.allowedPlacements.put((Player) e.getWhoClicked(), 1);
                }
            } else {
                // Cancel the event
                e.setCancelled(true);
                // Send the player a message
                e.getWhoClicked().sendMessage(ChatColor.RED + "You do not have permission to place a bed!");
            }
        }

    }

}
