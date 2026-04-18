package com.tcenema.magicwands;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
        // JOJO ABILITIES
        effects.put(WandType.THE_WORLD, (p, pl) -> {
            p.sendMessage("§e§lZA WARUDO!");
            p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BELL_RESONATE, 2, 0.5f);
            List<Entity> entities = p.getNearbyEntities(20, 20, 20);
            for (Entity e : entities) {
                if (e instanceof LivingEntity && e != p) {
                    LivingEntity le = (LivingEntity) e;
                    le.setAI(false);
                    new BukkitRunnable() {
                        @Override
                        public void run() { le.setAI(true); }
                    }.runTaskLater(pl, 100);
                }
            }
        });

        effects.put(WandType.STAR_PLATINUM, (p, pl) -> {
            p.sendMessage("§d§lORA ORA ORA!");
            new BukkitRunnable() {
                int count = 0;
                public void run() {
                    if (count++ > 20) cancel();
                    p.getWorld().spawnParticle(Particle.CRIT, p.getLocation().add(p.getLocation().getDirection().multiply(2)), 5);
                    p.getNearbyEntities(3, 3, 3).forEach(e -> {
                        if (e instanceof LivingEntity && e != p) ((LivingEntity) e).damage(2, p);
                    });
                }
            }.runTaskTimer(pl, 0, 1);
        });

        // DOMAIN EXPANSION
        effects.put(WandType.DOMAIN_VOID, (p, pl) -> {
            p.sendMessage("§5§lDomain Expansion: Infinite Void");
            Location center = p.getLocation();
            for (int i = 0; i < 360; i += 10) {
                double angle = Math.toRadians(i);
                double x = Math.cos(angle) * 10;
                double z = Math.sin(angle) * 10;
                p.getWorld().spawnParticle(Particle.REVERSE_PORTAL, center.clone().add(x, 1, z), 10);
            }
            p.getNearbyEntities(10, 10, 10).forEach(e -> {
                if (e instanceof LivingEntity && e != p) {
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 10));
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
                }
            });
        });

        // ELEMENTAL WANDS
        effects.put(WandType.ZEUS_BOLT, (p, pl) -> {
            Block target = p.getTargetBlockExact(50);
            if (target != null) p.getWorld().strikeLightning(target.getLocation());
        });

        effects.put(WandType.INFERNO_LANCE, (p, pl) -> {
            Fireball f = p.launchProjectile(Fireball.class);
            f.setYield(2);
            f.setIsIncendiary(true);
        });

        effects.put(WandType.FROST_AEGIS, (p, pl) -> {
            p.getNearbyEntities(5, 5, 5).forEach(e -> {
                if (e instanceof LivingEntity) ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 4));
            });
            p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 100, 2, 1, 2);
        });

        effects.put(WandType.MIDAS_HAND, (p, pl) -> {
            p.getNearbyEntities(5, 5, 5).forEach(e -> {
                if (e instanceof Item) {
                    Item item = (Item) e;
                    item.setItemStack(new org.bukkit.inventory.ItemStack(Material.GOLD_INGOT, item.getItemStack().getAmount()));
                }
            });
        });

        effects.put(WandType.BLACK_HOLE, (p, pl) -> {
            Location loc = p.getTargetBlockExact(20) != null ? p.getTargetBlockExact(20).getLocation() : p.getLocation().add(p.getLocation().getDirection().multiply(5));
            new BukkitRunnable() {
                int timer = 0;
                public void run() {
                    if (timer++ > 60) cancel();
                    loc.getWorld().spawnParticle(Particle.SQUID_INK, loc, 50, 0.5, 0.5, 0.5);
                    loc.getWorld().getNearbyEntities(loc, 7, 7, 7).forEach(e -> {
                        if (e != p) e.setVelocity(loc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.5));
                    });
                }
            }.runTaskTimer(pl, 0, 1);
        });

        effects.put(WandType.THOR_HAMMER, (p, pl) -> {
            p.getWorld().strikeLightning(p.getLocation());
            p.getNearbyEntities(10, 10, 10).forEach(e -> {
                if (e instanceof LivingEntity && e != p) ((LivingEntity) e).damage(10);
            });
        });

        effects.put(WandType.ENDER_PULSE, (p, pl) -> {
            Block b = p.getTargetBlockExact(50);
            if (b != null) {
                p.teleport(b.getLocation().add(0, 1, 0));
                p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 50);
                p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
        });

        effects.put(WandType.REAPER_SCYTHE, (p, pl) -> {
           p.getNearbyEntities(5, 5, 5).forEach(e -> {
               if (e instanceof LivingEntity && e != p) {
                   ((LivingEntity) e).damage(15);
                   p.getWorld().spawnParticle(Particle.SOUL, e.getLocation(), 20);
               }
           });
        });

        effects.put(WandType.GAME_MASTER, (p, pl) -> {
            p.setGameMode(p.getGameMode() == GameMode.CREATIVE ? GameMode.SURVIVAL : GameMode.CREATIVE);
            p.sendMessage("§b§lAuthority Toggled!");
        });

        // Fill remaining wands with basic effects if not explicitly defined yet
        for (WandType type : WandType.values()) {
            if (!effects.containsKey(type)) {
                effects.put(type, (p, pl) -> {
                    p.sendMessage("§aUsed magic: " + type.getDisplayName());
                    p.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, p.getLocation(), 30, 1, 1, 1);
                });
            }
        }
    }
}