package com.tcenema.magicwands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class MagicWands extends JavaPlugin {
    private static MagicWands instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new WandListener(this), this);
        registerRecipes();
        getLogger().info(ChatColor.AQUA + "[MagicWands] Module Online. Forging complete.");
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

    public static MagicWands getInstance() { return instance; }
}