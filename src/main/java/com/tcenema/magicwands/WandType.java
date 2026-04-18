package com.tcenema.magicwands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public enum WandType {
    FROST_AEGIS("Frost Aegis", NamedTextColor.AQUA, Material.BLUE_ICE, 15, List.of("bAbility: fGlacial Spikes", "8bb Creates a frozen arena.")),
    INFERNO_LANCE("Inferno Lance", NamedTextColor.RED, Material.NETHER_STAR, 10, List.of("cAbility: fHellfire Storm", "8bb Calls down fire.")),
    DOMAIN_VOID("Void Domain", NamedTextColor.DARK_PURPLE, Material.ENDER_EYE, 30, List.of("dAbility: fDomain Expansion", "8bb Traps enemies in void.")),
    STAR_PLATINUM("Star Platinum", NamedTextColor.DARK_PURPLE, Material.AMETHYST_SHARD, 20, List.of("5Ability: fORA ORA ORA!", "8bb Rapid strikes at your enemies.")),
    THE_WORLD("The World", NamedTextColor.YELLOW, Material.GOLD_INGOT, 60, List.of("eAbility: fZA WARUDO!", "8bb Stops time for 5 seconds.")),
    KILLER_QUEEN("Killer Queen", NamedTextColor.LIGHT_PURPLE, Material.TNT, 15, List.of("dAbility: fPrimary Bomb", "8bb Turns a target into a bomb."));

    private final String displayName;
    private final NamedTextColor color;
    private final Material core;
    private final int cooldown;
    private final List<String> lore;

    WandType(String displayName, NamedTextColor color, Material core, int cooldown, List<String> lore) {
        this.displayName = displayName;
        this.color = color;
        this.core = core;
        this.cooldown = cooldown;
        this.lore = lore;
    }

    public int getCooldown() { return cooldown; }
    public Material getRecipeCore() { return core; }
    public String getDisplayName() { return displayName; }
    public NamedTextColor getColor() { return color; }

    public ItemStack getItem(MagicWands plugin) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(displayName).color(color).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> finalLore = new ArrayList<>();
        for (String line : lore) finalLore.add(Component.text(line).decoration(TextDecoration.ITALIC, false));
        meta.lore(finalLore);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "wand_type"), PersistentDataType.STRING, this.name());
        item.setItemMeta(meta);
        return item;
    }
}