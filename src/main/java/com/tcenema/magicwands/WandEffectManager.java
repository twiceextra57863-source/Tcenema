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

        effects.put(WandType.KILLER_QUEEN, (p, pl) -> {
            p.sendMessage("§d§lKiller Queen has already touched that target!");
            Entity target = p.getTargetEntity(20);
            if (target != null) {
                p.getWorld().spawnParticle(Particle.FLAME, target.getLocation(), 50);
                new BukkitRunnable() {
                    public void run() {
                        target.getWorld().createExplosion(target.getLocation(), 4f, false, false);
                    }
                }.runTaskLater(pl, 20);
            } else {
                TNTPrimed tnt = p.getWorld().spawn(p.getLocation().add(p.getEyeLocation().getDirection()), TNTPrimed.class);
                tnt.setVelocity(p.getEyeLocation().getDirection().multiply(1.5));
                tnt.setFuseTicks(40);
            }
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

        // EPIC NEW ABILITIES
        effects.put(WandType.EARTH_QUAKE, (p, pl) -> {
            p.sendMessage("§2§lEARTHQUAKE!");
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 0.5f);
            for (int r = 1; r <= 8; r++) {
                final int radius = r;
                new BukkitRunnable() {
                    public void run() {
                        for (int i = 0; i < 360; i += 20) {
                            double angle = Math.toRadians(i);
                            Location loc = p.getLocation().add(Math.cos(angle) * radius, 0, Math.sin(angle) * radius);
                            p.getWorld().spawnParticle(Particle.BLOCK, loc, 10, 0.2, 0.1, 0.2, Material.DIRT.createBlockData());
                            loc.getWorld().getNearbyEntities(loc, 1.5, 2, 1.5).forEach(e -> {
                                if (e != p && e instanceof LivingEntity) {
                                    e.setVelocity(new Vector(0, 0.8, 0));
                                    ((LivingEntity) e).damage(4);
                                }
                            });
                        }
                    }
                }.runTaskLater(pl, r * 2);
            }
        });

        effects.put(WandType.METEOR_STRIKE, (p, pl) -> {
            p.sendMessage("§c§lMETEOR STRIKE!");
            Block target = p.getTargetBlockExact(100);
            Location targetLoc = target != null ? target.getLocation() : p.getLocation().add(p.getLocation().getDirection().multiply(20));
            Location spawnLoc = targetLoc.clone().add(5, 30, 5);
            Fireball fireball = spawnLoc.getWorld().spawn(spawnLoc, Fireball.class);
            fireball.setDirection(targetLoc.toVector().subtract(spawnLoc.toVector()).normalize());
            fireball.setYield(8);
            fireball.setIsIncendiary(true);
        });

        effects.put(WandType.PHOENIX_WING, (p, pl) -> {
            p.sendMessage("§6§lPHOENIX DASH!");
            p.setVelocity(p.getLocation().getDirection().multiply(2.5));
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
            new BukkitRunnable() {
                int i = 0;
                public void run() {
                    if (i++ > 15) cancel();
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 20, 0.2, 0.2, 0.2);
                    p.getNearbyEntities(2, 2, 2).forEach(e -> {
                        if (e != p) e.setFireTicks(100);
                    });
                }
            }.runTaskTimer(pl, 0, 1);
        });

        effects.put(WandType.SONIC_BOOM, (p, pl) -> {
            p.sendMessage("§3§lSONIC BOOM!");
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 2, 1);
            Vector dir = p.getLocation().getDirection();
            for (int i = 1; i < 20; i++) {
                Location loc = p.getEyeLocation().add(dir.clone().multiply(i));
                p.getWorld().spawnParticle(Particle.SONIC_BOOM, loc, 1);
                loc.getNearbyEntities(1.5, 1.5, 1.5).forEach(e -> {
                    if (e != p && e instanceof LivingEntity) {
                        ((LivingEntity) e).damage(12, p);
                        e.setVelocity(dir.clone().multiply(2).setY(0.5));
                    }
                });
            }
        });

        effects.put(WandType.NECROMANCER, (p, pl) -> {
            p.sendMessage("§2§lRise, my minions!");
            for (int i = 0; i < 4; i++) {
                Zombie zombie = (Zombie) p.getWorld().spawnEntity(p.getLocation().add(Math.random() * 3, 0, Math.random() * 3), EntityType.ZOMBIE);
                zombie.getEquipment().setHelmet(new org.bukkit.inventory.ItemStack(Material.IRON_HELMET));
                new BukkitRunnable() { public void run() { zombie.remove(); } }.runTaskLater(pl, 400);
            }
        });

        effects.put(WandType.VAMPIRE_FANG, (p, pl) -> {
            p.sendMessage("§4§lLIFE DRAIN!");
            p.getNearbyEntities(6, 6, 6).forEach(e -> {
                if (e instanceof LivingEntity && e != p) {
                    double dmg = 6.0;
                    ((LivingEntity) e).damage(dmg, p);
                    p.setHealth(Math.min(p.getMaxHealth(), p.getHealth() + (dmg / 2)));
                    p.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, e.getLocation(), 10);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1.5f);
                }
            });
        });

        effects.put(WandType.WIND_WEAVER, (p, pl) -> {
            p.sendMessage("§f§lSky Walk!");
            p.setVelocity(new Vector(0, 2, 0));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 0));
            p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 50, 1, 0.5, 1);
        });

        effects.put(WandType.SOLAR_FLARE, (p, pl) -> {
            p.sendMessage("§e§lSOLAR FLARE!");
            p.getWorld().playSound(p.getLocation(), Sound.ITEM_FIRECHARGE_USE, 2, 0.5f);
            p.getNearbyEntities(15, 15, 15).forEach(e -> {
                if (e instanceof LivingEntity && e != p) {
                    e.setFireTicks(200);
                    ((LivingEntity) e).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                    p.getWorld().spawnParticle(Particle.LAVA, e.getLocation(), 20);
                }
            });
            p.getWorld().spawnParticle(Particle.FLASH, p.getLocation(), 5);
        });

        effects.put(WandType.GRAVITY_WELL, (p, pl) -> {
            p.sendMessage("§9§lEvent Horizon!");
            Location loc = p.getTargetBlockExact(15) != null ? p.getTargetBlockExact(15).getLocation() : p.getLocation().add(p.getLocation().getDirection().multiply(5));
            new BukkitRunnable() {
                int t = 0;
                public void run() {
                    if (t++ > 100) cancel();
                    loc.getWorld().spawnParticle(Particle.PORTAL, loc, 30, 0.5, 0.5, 0.5);
                    loc.getWorld().getNearbyEntities(loc, 8, 8, 8).forEach(e -> {
                        if (e != p) e.setVelocity(loc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(0.2));
                    });
                }
            }.runTaskTimer(pl, 0, 1);
        });

        // PREVIOUS ABILITIES
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
