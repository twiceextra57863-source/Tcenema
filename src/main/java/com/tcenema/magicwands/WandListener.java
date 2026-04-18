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
                if (particlesEnabled) loc.getWorld().spawnParticle(Particle.WITCH, stand.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.05);
                if (ticks++ > 60) {
                    createBossBar(type, loc);
                    stand.remove();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1L);
    }

    private void createBossBar(WandType type, Location loc) {
        BossBar bar = Bukkit.createBossBar("§6§lRITUAL: §f" + type.getDisplayName(), BarColor.PURPLE, BarStyle.SOLID);
        Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
        new BukkitRunnable() {
            int time = 100; 
            public void run() {
                if (time-- <= 0) {
                    loc.getWorld().dropItemNaturally(loc, type.getItem(plugin));
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
            p.sendMessage("§cWait " + (timeLeft / 1000) + "s for the ether to recharge.");
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
        p.getWorld().playSound(loc, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f);
        switch (type) {
            case FROST_AEGIS -> {
                for (int i = 0; i < 360; i += 5) {
                    double angle = Math.toRadians(i);
                    Location spike = loc.clone().add(Math.cos(angle) * 5, 0, Math.sin(angle) * 5);
                    if (particlesEnabled) spike.getWorld().spawnParticle(Particle.SNOWFLAKE, spike, 10, 0.2, 2, 0.2, 0.05);
                }
                p.getNearbyEntities(5, 5, 5).stream().filter(e -> e instanceof LivingEntity && e != p).forEach(e -> ((LivingEntity) e).addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOW, 100, 2)));
            }
            case DOMAIN_VOID -> {
                activeDomains.put(p.getUniqueId(), type);
                p.sendMessage("§dDomain Expansion: Unlimited Void!");
                new BukkitRunnable() {
                    int t = 0;
                    public void run() {
                        if (particlesEnabled) p.getWorld().spawnParticle(Particle.DRAGON_BREATH, p.getLocation(), 50, 3, 3, 3, 0.05);
                        if (t++ > 200) {
                            activeDomains.remove(p.getUniqueId());
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 1L);
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