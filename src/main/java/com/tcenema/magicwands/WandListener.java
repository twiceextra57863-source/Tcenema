package com.tcenema.magicwands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;

public class WandListener implements Listener {
    private final MagicWands plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, WandType> activeDomains = new HashMap<>();
    public static boolean particlesEnabled = true;

    public WandListener(MagicWands plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getCurrentItem();
        if (result == null || !result.hasItemMeta()) return;
        String typeStr = result.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "wand_type"), PersistentDataType.STRING);
        if (typeStr == null) return;

        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        player.closeInventory();
        WandType type = WandType.valueOf(typeStr);
        startRitual(player, player.getLocation(), type);
    }

    private void startRitual(Player crafter, Location loc, WandType type) {
        crafter.showTitle(Title.title(Component.text("6lRITUAL START"), Component.text("7Forging the " + type.getDisplayName())));
        loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1f, 0.5f);

        new BukkitRunnable() {
            int ticks = 0;
            ArmorStand stand;
            public void run() {
                if (ticks == 0) {
                    stand = loc.getWorld().spawn(loc.clone().add(0, 1, 0), ArmorStand.class);
                    stand.setInvisible(true);
                    stand.setGravity(false);
                    stand.getEquipment().setItemInMainHand(type.getItem(plugin));
                }
                if (ticks < 40) {
                    stand.teleport(stand.getLocation().add(0, 0.05, 0));
                }
                stand.setRotation(stand.getLocation().getYaw() + 15, 0);
                if (particlesEnabled) loc.getWorld().spawnParticle(Particle.WITCH, stand.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.05);
                if (ticks++ > 60) {
                    createBossBar(type, loc);
                    stand.remove();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1L);
    }

    private void createBossBar(WandType type, Location loc) {
        BossBar bar = Bukkit.createBossBar("6lFORGING: f" + type.getDisplayName(), BarColor.PURPLE, BarStyle.SOLID);
        Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
        loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_SPAWN, 1f, 1f);
        new BukkitRunnable() {
            int time = 100; 
            public void run() {
                if (time-- <= 0) {
                    loc.getWorld().dropItemNaturally(loc, type.getItem(plugin));
                    loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 5);
                    loc.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    bar.removeAll();
                    this.cancel();
                }
                bar.setProgress(time / 100.0);
            }
        }.runTaskTimer(plugin, 0, 1L);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        String typeStr = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "wand_type"), PersistentDataType.STRING);
        if (typeStr == null) return;
        WandType type = WandType.valueOf(typeStr);

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (activeDomains.containsKey(p.getUniqueId())) shootLaser(p);
            return;
        }

        if (event.getAction().name().contains("RIGHT")) {
            if (checkCooldown(p, type)) {
                executeAbility(p, type);
            }
        }
    }

    private boolean checkCooldown(Player p, WandType type) {
        long lastUse = cooldowns.getOrDefault(p.getUniqueId(), 0L);
        long timeLeft = (lastUse + (type.getCooldown() * 1000L)) - System.currentTimeMillis();
        if (timeLeft > 0) {
            p.sendMessage("cWait " + (timeLeft / 1000) + "s for the stand power to recharge.");
            return false;
        }
        cooldowns.put(p.getUniqueId(), System.currentTimeMillis());
        return true;
    }

    private void shootLaser(Player p) {
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1f, 2f);
        RayTraceResult ray = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), 30, (e) -> !e.equals(p));
        if (ray != null && ray.getHitEntity() instanceof LivingEntity le) {
            le.damage(8.0, p);
            if (particlesEnabled) le.getWorld().spawnParticle(Particle.SONIC_BOOM, le.getLocation().add(0, 1, 0), 1);
        }
    }

    private void executeAbility(Player p, WandType type) {
        Location loc = p.getLocation();
        switch (type) {
            case STAR_PLATINUM -> {
                p.sendMessage("5lSTAR PLATINUM: ORA ORA ORA!");
                new BukkitRunnable() {
                    int i = 0;
                    public void run() {
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_PUNCH, 1f, 1.5f);
                        p.getNearbyEntities(4, 4, 4).forEach(e -> {
                            if (e instanceof LivingEntity le && e != p) {
                                Vector v = le.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(0.5);
                                le.setVelocity(v);
                                le.damage(2.0, p);
                            }
                        });
                        if (i++ > 15) this.cancel();
                    }
                }.runTaskTimer(plugin, 0, 2L);
            }
            case THE_WORLD -> {
                p.sendMessage("elZA WARUDO! TOKI WO TOMARE!");
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 2f, 0.5f);
                List<Entity> frozen = p.getNearbyEntities(15, 15, 15);
                frozen.forEach(e -> { if(e instanceof LivingEntity le) le.setAI(false); });
                new BukkitRunnable() {
                    public void run() {
                        frozen.forEach(e -> { if(e instanceof LivingEntity le) le.setAI(true); });
                        p.sendMessage("eTime begins to move again.");
                    }
                }.runTaskLater(plugin, 100L);
            }
            case KILLER_QUEEN -> {
                p.sendMessage("dKiller Queen has already touched that target.");
                RayTraceResult target = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), 15, (e) -> !e.equals(p));
                if (target != null && target.getHitEntity() != null) {
                    new BukkitRunnable() {
                        public void run() {
                           target.getHitEntity().getWorld().createExplosion(target.getHitEntity().getLocation(), 3f, false, false);
                        }
                    }.runTaskLater(plugin, 20L);
                }
            }
            case FROST_AEGIS -> {
                p.getNearbyEntities(5, 5, 5).stream().filter(e -> e instanceof LivingEntity && e != p).forEach(e -> ((LivingEntity) e).addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, 100, 2)));
            }
            case INFERNO_LANCE -> {
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 1000, 5, 2, 5, 0.02);
                p.getNearbyEntities(10, 5, 10).stream().filter(e -> e instanceof LivingEntity && e != p).forEach(e -> {
                    e.setFireTicks(100);
                    ((LivingEntity) e).damage(10.0, p);
                });
            }
        }
    }
}