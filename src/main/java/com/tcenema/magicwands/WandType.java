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
    FROST_AEGIS("Frost Aegis", NamedTextColor.AQUA, 15, "Glacial Spikes"),
    INFERNO_LANCE("Inferno Lance", NamedTextColor.RED, 10, "Hellfire Storm"),
    DOMAIN_VOID("Void Domain", NamedTextColor.DARK_PURPLE, 30, "Domain Expansion"),
    STAR_PLATINUM("Star Platinum", NamedTextColor.DARK_PURPLE, 20, "ORA ORA ORA!"),
    THE_WORLD("The World", NamedTextColor.YELLOW, 60, "ZA WARUDO!"),
    KILLER_QUEEN("Killer Queen", NamedTextColor.LIGHT_PURPLE, 15, "Primary Bomb"),
    THOR_HAMMER("Thor Hammer", NamedTextColor.GOLD, 12, "Mjolnir's Call"),
    EARTH_QUAKE("Earthquake", NamedTextColor.DARK_GREEN, 25, "Tectonic Shift"),
    GRAVITY_WELL("Gravity Well", NamedTextColor.BLUE, 20, "Singularity"),
    PHOENIX_WING("Phoenix Wing", NamedTextColor.GOLD, 15, "Rebirth Dash"),
    SONIC_BOOM("Sonic Boom", NamedTextColor.DARK_AQUA, 8, "Warden's Breath"),
    VAMPIRE_FANG("Vampire Fang", NamedTextColor.DARK_RED, 12, "Life Drain"),
    WIND_WEAVER("Wind Weaver", NamedTextColor.WHITE, 20, "Sky Walk"),
    POISON_IVY("Poison Ivy", NamedTextColor.GREEN, 10, "Toxic Cloud"),
    WITHER_TOUCH("Wither Touch", NamedTextColor.DARK_GRAY, 15, "Decay"),
    MIDAS_HAND("Midas Hand", NamedTextColor.YELLOW, 40, "Golden Touch"),
    CHRONOS_CLOCK("Chronos Clock", NamedTextColor.BLUE, 50, "Time Warp"),
    NEPTUNE_TRIDENT("Neptune", NamedTextColor.DARK_BLUE, 20, "Tidal Wave"),
    HADES_BREATH("Hades Breath", NamedTextColor.GRAY, 25, "Underworld Mist"),
    APOLLO_BOW("Apollo Bow", NamedTextColor.GOLD, 10, "Solar Arrow"),
    HERMES_BOOTS("Hermes Speed", NamedTextColor.WHITE, 15, "God Speed"),
    ZEUS_BOLT("Zeus Bolt", NamedTextColor.AQUA, 5, "Lightning Strike"),
    GOLEM_HEART("Golem Heart", NamedTextColor.GRAY, 30, "Unstoppable Force"),
    REAPER_SCYTHE("Reaper Scythe", NamedTextColor.BLACK, 45, "Soul Harvest"),
    MEDIC_KIT("Medic Kit", NamedTextColor.RED, 20, "Holy Heal"),
    SHADOW_STEP("Shadow Step", NamedTextColor.DARK_GRAY, 5, "Abyssal Blink"),
    LIGHTNING_DASH("Lightning Dash", NamedTextColor.YELLOW, 8, "Volt Sprint"),
    MAGMA_BURST("Magma Burst", NamedTextColor.RED, 15, "Volcanic Eruption"),
    BLIZZARD_STORM("Blizzard Storm", NamedTextColor.WHITE, 20, "Arctic Blast"),
    SOLAR_FLARE("Solar Flare", NamedTextColor.GOLD, 30, "Sun Burn"),
    LUNAR_ECLIPSE("Lunar Eclipse", NamedTextColor.BLUE, 30, "Moon Shadow"),
    JUNGLE_WRATH("Jungle Wrath", NamedTextColor.GREEN, 15, "Nature's Revenge"),
    DESERT_MIRAGE("Desert Mirage", NamedTextColor.YELLOW, 25, "Sand Trap"),
    OCEAN_SURGE("Ocean Surge", NamedTextColor.BLUE, 10, "Hydro Pump"),
    ENDER_PULSE("Ender Pulse", NamedTextColor.DARK_PURPLE, 5, "Void Warp"),
    SHULKER_SHELL("Shulker Shell", NamedTextColor.LIGHT_PURPLE, 15, "Levitation Burst"),
    IRON_MAIDEN("Iron Maiden", NamedTextColor.GRAY, 20, "Steel Trap"),
    DRAGON_BREATH("Dragon Breath", NamedTextColor.DARK_PURPLE, 20, "Ender Fire"),
    SOUL_STEALER("Soul Stealer", NamedTextColor.DARK_AQUA, 15, "Exp Drain"),
    BLACK_HOLE("Black Hole", NamedTextColor.BLACK, 60, "Event Horizon"),
    HOLY_LIGHT("Holy Light", NamedTextColor.WHITE, 10, "Exorcism"),
    NECROMANCER("Necromancer", NamedTextColor.DARK_GREEN, 40, "Undead Army"),
    THUNDER_CLAP("Thunder Clap", NamedTextColor.GOLD, 5, "Sonic Shock"),
    FROST_BITE("Frostbite", NamedTextColor.AQUA, 15, "Deep Freeze"),
    PYROMANIAC("Pyromaniac", NamedTextColor.RED, 10, "Fire Storm"),
    GUARDIAN_SHIELD("Guardian", NamedTextColor.BLUE, 30, "Elder Protection"),
    BERSERKER_RAGE("Berserker", NamedTextColor.DARK_RED, 40, "Blood Lust"),
    NINJA_VANISH("Ninja Vanish", NamedTextColor.DARK_GRAY, 20, "Smoke Bomb"),
    METEOR_STRIKE("Meteor Strike", NamedTextColor.RED, 50, "Armageddon"),
    GAME_MASTER("Game Master", NamedTextColor.LIGHT_PURPLE, 0, "Absolute Authority");

    private final String displayName;
    private final NamedTextColor color;
    private final int cooldown;
    private final String ability;

    WandType(String displayName, NamedTextColor color, int cooldown, String ability) {
        this.displayName = displayName;
        this.color = color;
        this.cooldown = cooldown;
        this.ability = ability;
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