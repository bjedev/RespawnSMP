package dev.fluyd.respawnsmp;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class RespawnSMP extends JavaPlugin {
    public static RespawnSMP INSTANCE;
    public static HashMap<Player, Integer> allowedPlacements = new HashMap<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        // Delete all bed recipes so the only one remaining is our custom one
        RespawnSMPRecipe.removeRecipies();
        RespawnSMPRecipe.addRecipe();

        // Register the listeners
        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
