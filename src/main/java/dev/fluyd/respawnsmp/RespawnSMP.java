package dev.fluyd.respawnsmp;

import dev.fluyd.respawnsmp.utils.Serialize;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;

public final class RespawnSMP extends JavaPlugin {
    public static RespawnSMP INSTANCE;
    public HashMap<Player, Integer> amountAllowedToCraft = new HashMap<>();
    public HashMap<Player, Integer> amountPlaced = new HashMap<>();
    public HashMap<Player, ArrayList<Location>> spawnPoints = new HashMap<>();
    public ArrayList<Player> deadPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        // Delete all bed recipes so the only one remaining is our custom one
        RespawnSMPRecipe.removeRecipies();
        RespawnSMPRecipe.addRecipe();
        RespawnSMPRecipe.addCompassRecipe();
        RespawnSMPRecipe.addReviveItem();

        // Load the previously serialized data
        try {
            amountAllowedToCraft = (HashMap<Player, Integer>) Serialize.deserialize("amountAllowedToCraft");
            amountPlaced = (HashMap<Player, Integer>) Serialize.deserialize("amountPlaced");
            spawnPoints = (HashMap<Player, ArrayList<Location>>) Serialize.deserialize("spawnPoints");
            deadPlayers = (ArrayList<Player>) Serialize.deserialize("deadPlayers");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Register the listeners
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        // Register admin commands
        registerCommand("setplacedbeds", (player, args) -> {
            // Args: <amount> Optional: <player>

            // Verify that the amount arg is not more than 3 or less than 0
            if (args.length >= 1) {
                int amount = Integer.parseInt(args[0]);
                if (amount > 3 || amount < 0) {
                    player.sendMessage(ChatColor.RED + "Amount must be between 0 and 3!");
                    return;
                }
            }

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /setplacedbeds <amount> [player]");
            } else if (args.length == 1) {
                amountPlaced.put(player, Integer.parseInt(args[0]));
                player.sendMessage(ChatColor.GREEN + "Set amount of placed beds to " + ChatColor.YELLOW + args[0]);
            } else if (args.length == 2) {
                Player target = getServer().getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                amountPlaced.put(target, Integer.parseInt(args[0]));
                player.sendMessage(ChatColor.GREEN + "Set amount of placed beds to " + ChatColor.YELLOW + args[0] + ChatColor.GREEN + " for " + ChatColor.YELLOW + target.getName());
            }
        });

        // List player spawnpoints
        registerCommand("spawnpoints", (player, args) -> {
            // Args: <player>

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /spawnpoints <player>");
            } else if (args.length == 1) {
                Player target = getServer().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                if (spawnPoints.get(target) == null || spawnPoints.get(target).size() == 0) {
                    player.sendMessage(ChatColor.RED + "Player has no spawnpoints!");
                    return;
                }
                player.sendMessage(ChatColor.GREEN + "Spawnpoints for " + ChatColor.YELLOW + target.getName() + ChatColor.GREEN + ":");
                for (Location location : spawnPoints.get(target)) {
                    player.sendMessage(ChatColor.GREEN + " - " + ChatColor.YELLOW + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
                }
            }
        });

        // Clear player spawnpoints
        registerCommand("clearspawnpoints", (player, args) -> {
            // Args: <player>

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Usage: /clearspawnpoints <player>");
            } else if (args.length == 1) {
                Player target = getServer().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                spawnPoints.put(target, new ArrayList<>());
                player.sendMessage(ChatColor.GREEN + "Cleared spawnpoints for " + ChatColor.YELLOW + target.getName());
            }
        });
    }

    @Override
    public void onDisable() {
        try {
            Serialize.serialize(amountAllowedToCraft, "amountAllowedToCraft");
            Serialize.serialize(amountPlaced, "amountPlaced");
            Serialize.serialize(spawnPoints, "spawnPoints");
            Serialize.serialize(deadPlayers, "deadPlayers");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerCommand(String command, BiConsumer<Player, String[]> executor) {
        getCommand(command).setExecutor((sender, command1, label, args) -> {
            executor.accept((Player) sender, args);
            return true;
        });
    }
}
