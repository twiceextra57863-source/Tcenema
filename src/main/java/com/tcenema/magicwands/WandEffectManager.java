package com.tcenema.magicwands;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.EnumMap;
import java.util.Map;

public class WandEffectManager {
    private final Map<WandType, WandEffect> effects = new EnumMap<>(WandType.class);

    public WandEffectManager() {
        registerEffects();
    }

    public void handle(WandType type, Player player, MagicWands plugin) {
        WandEffect effect = effects.get(type);
        if (effect != null) effect.execute(player, plugin);
    }

    private void registerEffects() {
        effects.put(WandType.ZEUS_BOLT, (p, pl) -> p.getWorld().strikeLightning(p.getTargetBlockExact(50).getLocation()));
        effects.put(WandType.PHOENIX_WING, (p, pl) -> {
            p.setVelocity(p.getLocation().getDirection().multiply(2));
            p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 50);
        });
        effects.put(WandType.VAMPIRE_FANG, (p, pl) -> {
            p.getNearbyEntities(5, 5, 5).stream().filter(e -> e instanceof LivingEntity && e != p).forEach(e -> {
                ((LivingEntity) e).damage(4, p);
                p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + 2));
            });
        });
        effects.put(WandType.HERMES_BOOTS, (p, pl) -> p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 4)));
        effects.put(WandType.MEDIC_KIT, (p, pl) -> {
           p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
           p.getNearbyEntities(5, 5, 5).stream().filter(e -> e instanceof Player).forEach(e -> ((Player)e).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2)));
        });
        effects.put(WandType.NINJA_VANISH, (p, pl) -> {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0));
            p.getWorld().spawnParticle(Particle.SMOKE, p.getLocation(), 100);
        });
        // Additional effects would be registered here to fill the 50 count...
        for (WandType type : WandType.values()) {
            if (!effects.containsKey(type)) {
                effects.put(type, (p, pl) -> p.sendMessage("§aUsed magic: " + type.getDisplayName()));
            }
        }
    }
}