package com.tcenema.magicwands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicWands extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        getCommand("wand").setExecutor(new WandCommand(this));
        registerRecipes();
        getLogger().info("§b[MagicWands] Core Online. System Secure.");
    }

    private void registerRecipes() {
        for (WandType type : WandType.values()) {
            ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, type.name().toLowerCase() + "_recipe"), type.getItem(this));
            recipe.shape("  D", " S ", "S  ");
            recipe.setIngredient('S', Material.STICK);
            recipe.setIngredient('D', type.getRecipeCore());
            Bukkit.addRecipe(recipe);
        }
    }
}