package dev.fluyd.respawnsmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Iterator;

public class RespawnSMPRecipe {

    public static void removeRecipies() {
        // Iterate through all registered recipes
        for (Iterator<Recipe> it = Bukkit.getServer().recipeIterator(); it.hasNext(); ) {
            Recipe recipe = it.next();
            // Check if the recipe is a bed recipe
            if (recipe instanceof ShapedRecipe) {
                ItemStack result = recipe.getResult();
                if (result.getType() == Material.WHITE_BED || result.getType() == Material.ORANGE_BED
                        || result.getType() == Material.MAGENTA_BED || result.getType() == Material.LIGHT_BLUE_BED
                        || result.getType() == Material.YELLOW_BED || result.getType() == Material.LIME_BED
                        || result.getType() == Material.PINK_BED || result.getType() == Material.GRAY_BED
                        || result.getType() == Material.LIGHT_GRAY_BED || result.getType() == Material.CYAN_BED
                        || result.getType() == Material.PURPLE_BED || result.getType() == Material.BLUE_BED
                        || result.getType() == Material.BROWN_BED || result.getType() == Material.GREEN_BED
                        || result.getType() == Material.RED_BED || result.getType() == Material.BLACK_BED
                        || result.getType() == Material.COMPASS) {
                    // Remove the bed recipe
                    it.remove();
                }
            }
        }
    }

    public static void addRecipe() {
        // Create a new recipe for a bed
        // Pattern
        // AIR NETHERITE_INGOT AIR
        // WOOL WOOL WOOL
        // MANGROVE_PLANKS MANGROVE_PLANKS MANGROVE_PLANKS

        // Create the BED item
        ItemStack bed = new ItemStack(Material.RED_BED);

        // Create the recipe
        ShapedRecipe bedRecipe = new ShapedRecipe(new NamespacedKey(RespawnSMP.INSTANCE, "custom_bed"), bed);
        bedRecipe.shape(" N ", "WWW", "PPP");
        bedRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        bedRecipe.setIngredient('W', Material.WHITE_WOOL);
        bedRecipe.setIngredient('P', Material.MANGROVE_LOG);

        // Add the recipe to the server
        Bukkit.getServer().addRecipe(bedRecipe);
    }

    public static void addReviveItem() {
        // DIAMOND DIAMOND DIAMOND
        // DIAMOND NETHERITE_INGOT DIAMOND
        // DIAMOND DIAMOND DIAMOND
        NamespacedKey reviveKey = new NamespacedKey(RespawnSMP.INSTANCE, "revive_item");
        ShapedRecipe reviveItemRecipe = new ShapedRecipe(reviveKey, new ItemStack(Material.BARRIER));
        reviveItemRecipe.shape("DDD", "DND", "DDD");
        reviveItemRecipe.setIngredient('D', Material.DIAMOND);
        reviveItemRecipe.setIngredient('N', Material.NETHERITE_INGOT);
        Bukkit.addRecipe(reviveItemRecipe);
    }

    public static void addCompassRecipe() {
        // AIR DIAMOND AIR
        // DIAMOND GOLDEN_APPLE DIAMOND
        // AIR DIAMOND AIR

        NamespacedKey locatorKey = new NamespacedKey(RespawnSMP.INSTANCE, "bed_locator");
        ShapedRecipe bedLocatorRecipe = new ShapedRecipe(locatorKey, new ItemStack(Material.COMPASS));
        bedLocatorRecipe.shape(" A ", "ADA", " A ");
        bedLocatorRecipe.setIngredient('A', Material.DIAMOND);
        bedLocatorRecipe.setIngredient('D', Material.GOLDEN_APPLE);
        Bukkit.addRecipe(bedLocatorRecipe);
    }
}
