package com.tcenema.magicwands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public enum WandType {
    FROST_AEGIS("Frost Aegis", NamedTextColor.AQUA, Material.BLUE_ICE),
    INFERNO_LANCE("Inferno Lance", NamedTextColor.RED, Material.NETHER_STAR),
    VOID_RIFT("Void Rift", NamedTextColor.DARK_PURPLE, Material.ENDER_EYE);

    private final String name;
    private final NamedTextColor color;
    private final Material core;

    WandType(String name, NamedTextColor color, Material core) {
        this.name = name;
        this.color = color;
        this.core = core;
    }

    public Material getRecipeCore() { return core; }

    public ItemStack getItem(MagicWands plugin) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).color(color).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.lore(List.of(
            Component.text("Movie-grade artifact").color(NamedTextColor.GRAY),
            Component.text("Right-click to unleash power").color(NamedTextColor.YELLOW)
        ));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "wand_type"), PersistentDataType.STRING, this.name());
        item.setItemMeta(meta);
        return item;
    }
}