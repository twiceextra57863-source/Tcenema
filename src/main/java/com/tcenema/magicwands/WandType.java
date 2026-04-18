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
    FROST_AEGIS("Frost Aegis", NamedTextColor.AQUA, 15, "Glacial Spikes", Material.BLUE_ICE),
    INFERNO_LANCE("Inferno Lance", NamedTextColor.RED, 10, "Hellfire Storm", Material.BLAZE_POWDER),
    DOMAIN_VOID("Void Domain", NamedTextColor.DARK_PURPLE, 30, "Domain Expansion", Material.ENDER_EYE),
    STAR_PLATINUM("Star Platinum", NamedTextColor.DARK_PURPLE, 20, "ORA ORA ORA!", Material.AMETHYST_SHARD),
    THE_WORLD("The World", NamedTextColor.YELLOW, 60, "ZA WARUDO!", Material.CLOCK),
    KILLER_QUEEN("Killer Queen", NamedTextColor.LIGHT_PURPLE, 15, "Primary Bomb", Material.TNT),
    THOR_HAMMER("Thor Hammer", NamedTextColor.GOLD, 12, "Mjolnir's Call", Material.IRON_BLOCK),
    EARTH_QUAKE("Earthquake", NamedTextColor.DARK_GREEN, 25, "Tectonic Shift", Material.DIRT),
    GRAVITY_WELL("Gravity Well", NamedTextColor.BLUE, 20, "Singularity", Material.OBSIDIAN),
    PHOENIX_WING("Phoenix Wing", NamedTextColor.GOLD, 15, "Rebirth Dash", Material.FEATHER),
    SONIC_BOOM("Sonic Boom", NamedTextColor.DARK_AQUA, 8, "Warden's Breath", Material.ECHO_SHARD),
    VAMPIRE_FANG("Vampire Fang", NamedTextColor.DARK_RED, 12, "Life Drain", Material.FERMENTED_SPIDER_EYE),
    WIND_WEAVER("Wind Weaver", NamedTextColor.WHITE, 20, "Sky Walk", Material.FEATHER),
    POISON_IVY("Poison Ivy", NamedTextColor.GREEN, 10, "Toxic Cloud", Material.POISONOUS_POTATO),
    WITHER_TOUCH("Wither Touch", NamedTextColor.DARK_GRAY, 15, "Decay", Material.WITHER_SKELETON_SKULL),
    MIDAS_HAND("Midas Hand", NamedTextColor.YELLOW, 40, "Golden Touch", Material.GOLD_BLOCK),
    CHRONOS_CLOCK("Chronos Clock", NamedTextColor.BLUE, 50, "Time Warp", Material.CLOCK),
    NEPTUNE_TRIDENT("Neptune", NamedTextColor.DARK_BLUE, 20, "Tidal Wave", Material.PRISMARINE_CRYSTALS),
    HADES_BREATH("Hades Breath", NamedTextColor.GRAY, 25, "Underworld Mist", Material.SOUL_SOIL),
    APOLLO_BOW("Apollo Bow", NamedTextColor.GOLD, 10, "Solar Arrow", Material.SPECTRAL_ARROW),
    HERMES_BOOTS("Hermes Speed", NamedTextColor.WHITE, 15, "God Speed", Material.RABBIT_FOOT),
    ZEUS_BOLT("Zeus Bolt", NamedTextColor.AQUA, 5, "Lightning Strike", Material.LIGHTNING_ROD),
    GOLEM_HEART("Golem Heart", NamedTextColor.GRAY, 30, "Unstoppable Force", Material.IRON_GOLEM_SPAWN_EGG),
    REAPER_SCYTHE("Reaper Scythe", NamedTextColor.BLACK, 45, "Soul Harvest", Material.NETHERITE_SCRAP),
    MEDIC_KIT("Medic Kit", NamedTextColor.RED, 20, "Holy Heal", Material.GOLDEN_APPLE),
    SHADOW_STEP("Shadow Step", NamedTextColor.DARK_GRAY, 5, "Abyssal Blink", Material.ENDER_PEARL),
    LIGHTNING_DASH("Lightning Dash", NamedTextColor.YELLOW, 8, "Volt Sprint", Material.COPPER_INGOT),
    MAGMA_BURST("Magma Burst", NamedTextColor.RED, 15, "Volcanic Eruption", Material.MAGMA_BLOCK),
    BLIZZARD_STORM("Blizzard Storm", NamedTextColor.WHITE, 20, "Arctic Blast", Material.SNOW_BLOCK),
    SOLAR_FLARE("Solar Flare", NamedTextColor.GOLD, 30, "Sun Burn", Material.GLOWSTONE),
    LUNAR_ECLIPSE("Lunar Eclipse", NamedTextColor.BLUE, 30, "Moon Shadow", Material.LAPIS_BLOCK),
    JUNGLE_WRATH("Jungle Wrath", NamedTextColor.GREEN, 15, "Nature's Revenge", Material.OAK_LOG),
    DESERT_MIRAGE("Desert Mirage", NamedTextColor.YELLOW, 25, "Sand Trap", Material.SAND),
    OCEAN_SURGE("Ocean Surge", NamedTextColor.BLUE, 10, "Hydro Pump", Material.WATER_BUCKET),
    ENDER_PULSE("Ender Pulse", NamedTextColor.DARK_PURPLE, 5, "Void Warp", Material.ENDER_PEARL),
    SHULKER_SHELL("Shulker Shell", NamedTextColor.LIGHT_PURPLE, 15, "Levitation Burst", Material.SHULKER_SHELL),
    IRON_MAIDEN("Iron Maiden", NamedTextColor.GRAY, 20, "Steel Trap", Material.IRON_BARS),
    DRAGON_BREATH("Dragon Breath", NamedTextColor.DARK_PURPLE, 20, "Ender Fire", Material.DRAGON_BREATH),
    SOUL_STEALER("Soul Stealer", NamedTextColor.DARK_AQUA, 15, "Exp Drain", Material.EXPERIENCE_BOTTLE),
    BLACK_HOLE("Black Hole", NamedTextColor.BLACK, 60, "Event Horizon", Material.NETHER_STAR),
    HOLY_LIGHT("Holy Light", NamedTextColor.WHITE, 10, "Exorcism", Material.SEA_LANTERN),
    NECROMANCER("Necromancer", NamedTextColor.DARK_GREEN, 40, "Undead Army", Material.BONE_BLOCK),
    THUNDER_CLAP("Thunder Clap", NamedTextColor.GOLD, 5, "Sonic Shock", Material.COPPER_BLOCK),
    FROST_BITE("Frostbite", NamedTextColor.AQUA, 15, "Deep Freeze", Material.ICE),
    PYROMANIAC("Pyromaniac", NamedTextColor.RED, 10, "Fire Storm", Material.FIRE_CHARGE),
    GUARDIAN_SHIELD("Guardian", NamedTextColor.BLUE, 30, "Elder Protection", Material.HEART_OF_THE_SEA),
    BERSERKER_RAGE("Berserker", NamedTextColor.DARK_RED, 40, "Blood Lust", Material.REDSTONE_BLOCK),
    NINJA_VANISH("Ninja Vanish", NamedTextColor.DARK_GRAY, 20, "Smoke Bomb", Material.GUNPOWDER),
    METEOR_STRIKE("Meteor Strike", NamedTextColor.RED, 50, "Armageddon", Material.MAGMA_CREAM),
    GAME_MASTER("Game Master", NamedTextColor.LIGHT_PURPLE, 0, "Absolute Authority", Material.NETHER_STAR);

    private final String displayName;
    private final NamedTextColor color;
    private final int cooldown;
    private final String ability;
    private final Material recipeCore;

    WandType(String displayName, NamedTextColor color, int cooldown, String ability, Material recipeCore) {
        this.displayName = displayName;
        this.color = color;
        this.cooldown = cooldown;
        this.ability = ability;
        this.recipeCore = recipeCore;
    }

    public int getCooldown() {
        return cooldown;
    }

    public String getDisplayName() {
        return displayName;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public Material getRecipeCore() {
        return recipeCore;
    }

    public ItemStack getItem(MagicWands plugin) {
        ItemStack item = new ItemStack(Material.STONE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(displayName).color(color).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("§7Ability: §f" + ability).decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("§7Cooldown: §b" + cooldown + "s").decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "wand_type"), PersistentDataType.STRING, this.name());
        item.setItemMeta(meta);
        return item;
    }
}