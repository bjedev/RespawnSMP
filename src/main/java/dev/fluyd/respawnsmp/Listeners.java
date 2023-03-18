package dev.fluyd.respawnsmp;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Listeners implements Listener {
    @EventHandler
    public void onBedBreak(BlockBreakEvent e) {
        // Check if the broken block is a bed
        if (e.getBlock().getType() == Material.RED_BED) {
            Player player = e.getPlayer();

            // Only subtract 1 from the amount of beds the player has placed if the player has placed more than 0 beds
            if (RespawnSMP.INSTANCE.amountPlaced.get(player) > 0) {
                // Break the bed and decrease the amount of beds the player has placed
                RespawnSMP.INSTANCE.amountPlaced.put(player, RespawnSMP.INSTANCE.amountPlaced.get(player) - 1);
                removePossibleSpawnpoint(player, e.getBlock().getLocation());
            }
        }
    }
    @EventHandler
    public void onBedPlace(BlockPlaceEvent e) {
        // Check if the placed block is a bed
        if (e.getBlock().getType() == Material.RED_BED) {
            Player player = e.getPlayer();

            // Check if the player has already placed a bed, we allow a max of 3 beds per player
            if (RespawnSMP.INSTANCE.amountPlaced.containsKey(player)) {
                // Check if the player has already placed 3 beds
                if (RespawnSMP.INSTANCE.amountPlaced.get(player) == 3) {
                    // Cancel the event
                    e.setCancelled(true);
                    // Send the player a message
                    player.sendMessage(ChatColor.RED + "You have already placed 3 beds!");
                } else {
                    // Add 1 to the amount of beds the player has placed
                    RespawnSMP.INSTANCE.amountPlaced.put(player, RespawnSMP.INSTANCE.amountPlaced.get(player) + 1);
                    addPossibleSpawnpoint(player, e.getBlock().getLocation());
                }
            } else {
                // Add the player to the hashmap
                RespawnSMP.INSTANCE.amountPlaced.put(player, 1);
                addPossibleSpawnpoint(player, e.getBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onCompassLink(PlayerInteractEntityEvent e) {
        // make sure it was on click down and not relase
        if (e.getHand() == null || e.getHand() == EquipmentSlot.OFF_HAND) return;

        // Check if the item in the player's hand is lodestone compass, make it point towards the nearest bed of the right clicked player
        if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.COMPASS && !e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(RespawnSMP.INSTANCE, "used"), PersistentDataType.INTEGER)) {
            // Check if the player right clicked a player
            if (e.getRightClicked() instanceof Player) {
                Player player = (Player) e.getRightClicked();

                // Check if the player has placed any beds
                if (RespawnSMP.INSTANCE.amountPlaced.containsKey(player)) {
                    // Check if the player has placed at least 1 bed
                    if (RespawnSMP.INSTANCE.amountPlaced.get(player) > 0) {
                        // Get the player's possible spawnpoints
                        ArrayList<Location> possibleSpawnpoints = RespawnSMP.INSTANCE.spawnPoints.get(player);

                        // Check if the player has any possible spawnpoints
                        if (possibleSpawnpoints != null) {
                            // Get the nearest spawnpoint
                            Location nearestSpawnpoint = possibleSpawnpoints.get(0);

                            // Loop through all the spawnpoints
                            for (Location spawnpoint : possibleSpawnpoints) {
                                // Check if the spawnpoint is closer than the current nearest spawnpoint
                                if (spawnpoint.distanceSquared(player.getLocation()) < nearestSpawnpoint.distanceSquared(player.getLocation())) {
                                    // Set the nearest spawnpoint to the current spawnpoint
                                    nearestSpawnpoint = spawnpoint;
                                }
                            }

                            // Set the compass pointing location
                            e.getPlayer().setCompassTarget(nearestSpawnpoint);
                            // Set the item's name to the player's name
                            ItemMeta itemMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
                            itemMeta.setDisplayName(ChatColor.YELLOW + player.getName() + "'s " + ChatColor.GREEN + "compass");
                            itemMeta.getPersistentDataContainer().set(new NamespacedKey(RespawnSMP.INSTANCE, "used"), PersistentDataType.INTEGER, 1);
                            e.getPlayer().getInventory().getItemInMainHand().setItemMeta(itemMeta);
                        }
                    } else {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(ChatColor.RED + "That player has not placed any beds! *cough *cough* maybe you should kill 'em now...");
                    }
                }
            }
        } else if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(RespawnSMP.INSTANCE, "used"), PersistentDataType.INTEGER) && e.getRightClicked() instanceof Player) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You have already used that compass!");
        } else if (!(e.getRightClicked() instanceof Player)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You can only link a compass to a player!");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        // Check if the player has placed any beds
        if (RespawnSMP.INSTANCE.amountPlaced.containsKey(player)) {
            // Check if the player has placed at least 1 bed
            if (RespawnSMP.INSTANCE.amountPlaced.get(player) > 0) {
                // Get the player's possible spawnpoints
                ArrayList<Location> possibleSpawnpoints = RespawnSMP.INSTANCE.spawnPoints.get(player);

                // Check if the player has any possible spawnpoints
                if (possibleSpawnpoints != null) {
                    // Get a random spawnpoint from the list
                    Location spawnpoint = possibleSpawnpoints.get((int) (Math.random() * possibleSpawnpoints.size()));

                    // Set the respawn location to the random spawnpoint
                    e.setRespawnLocation(spawnpoint);
                }
            } else {
                // Put the player in the spectator gamemode and send them a message saying "You have no beds! Wait for somebody to revive you!"
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(ChatColor.RED + "You have no beds! Wait for somebody to revive you!");
                RespawnSMP.INSTANCE.deadPlayers.add(player);
            }
        } else {
            // Put the player in the spectator gamemode and send them a message saying "You have no beds! Wait for somebody to revive you!"
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.RED + "You have no beds! Wait for somebody to revive you!");
            RespawnSMP.INSTANCE.deadPlayers.add(player);
        }
    }

    private void addPossibleSpawnpoint(Player player, Location location) {
        // Get the current possible spawnpoints for the player
        ArrayList<Location> possibleSpawnpoints = RespawnSMP.INSTANCE.spawnPoints.get(player);

        // Check if the player has any possible spawnpoints yet, otherwise create a new ArrayList
        if (possibleSpawnpoints == null) {
            possibleSpawnpoints = new ArrayList<>();
        }

        // Add the new spawnpoint to the list
        possibleSpawnpoints.add(location);

        // Save the new list of spawnpoints
        RespawnSMP.INSTANCE.spawnPoints.put(player, possibleSpawnpoints);
    }

    public void removePossibleSpawnpoint(Player player, Location location) {
        // Get the player's possible spawnpoints
        ArrayList<Location> possibleSpawnpoints = RespawnSMP.INSTANCE.spawnPoints.get(player);

        // Remove the specified spawnpoint from the list
        possibleSpawnpoints.remove(location);
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        System.out.println(RespawnSMP.INSTANCE.amountAllowedToCraft);

        // Check if the result is RED_BED
        if (e.getRecipe().getResult().getType() == Material.RED_BED) {
            Player player = (Player) e.getWhoClicked();

            // Check if the player has already crafted a bed
            if (RespawnSMP.INSTANCE.amountAllowedToCraft.containsKey(player)) {
                // Check if the player has already crafted 1 bed
                if (RespawnSMP.INSTANCE.amountAllowedToCraft.get(player) == 1) {
                    // Cancel the event
                    e.setCancelled(true);
                    // Send the player a message
                    player.sendMessage(ChatColor.RED + "You have already crafted a bed!");
                } else {
                    // Add 1 to the amount of beds the player has crafted
                    RespawnSMP.INSTANCE.amountAllowedToCraft.put(player, RespawnSMP.INSTANCE.amountAllowedToCraft.get(player) + 1);
                }
            } else {
                // Add the player to the hashmap
                RespawnSMP.INSTANCE.amountAllowedToCraft.put(player, 1);
            }
        }
    }

    @EventHandler
    public void onReviveItemUsed(PlayerInteractEvent e) {
        // Check if the player right clicks with a barrier in their hand, get the item name and rename it to who you want it to revive, if they are not dead then send a message saying they are not dead
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(RespawnSMP.INSTANCE, "used"), PersistentDataType.INTEGER)) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED + "You have already used that item!");
                } else {
                    String name = e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName();
                    Player player = Bukkit.getPlayer(name);

                    if (RespawnSMP.INSTANCE.deadPlayers.contains(player)) {
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage(ChatColor.GREEN + "You have been revived by " + ChatColor.YELLOW + e.getPlayer().getName() + ChatColor.GREEN + "!");
                        e.getPlayer().sendMessage(ChatColor.GREEN + "You have revived " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "!");
                        RespawnSMP.INSTANCE.deadPlayers.remove(player);
                        ItemMeta itemMeta = e.getPlayer().getInventory().getItemInMainHand().getItemMeta();
                        e.getPlayer().getInventory().getItemInMainHand().setType(Material.NETHERITE_SCRAP);
                        itemMeta.setDisplayName(ChatColor.YELLOW + e.getPlayer().getName() + "'s " + ChatColor.GREEN + "revive item");
                        itemMeta.getPersistentDataContainer().set(new NamespacedKey(RespawnSMP.INSTANCE, "used"), PersistentDataType.INTEGER, 1);
                        e.getPlayer().getInventory().getItemInMainHand().setItemMeta(itemMeta);
                    } else {
                        e.getPlayer().sendMessage(ChatColor.RED + "That player is not dead!");
                    }
                }

                e.setCancelled(true);
            }
        }
    }
}
