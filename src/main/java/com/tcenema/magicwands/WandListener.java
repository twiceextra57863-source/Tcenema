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
import org.bukkit.util.Vector;

import java.util.*;

public class WandListener implements Listener {
    private final MagicWands plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, WandType> activeDomains = new HashMap<>();

    public WandListener(MagicWands plugin) { this.plugin = plugin; }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        ItemStack result = event.getCurrentItem();
        if (result == null || !result.hasItemMeta()) return;
        String typeStr = result.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "wand_type"), PersistentDataType.STRING);
        if (typeStr == null) return;

        Player player = (Player) event.getWhoClicked();
        Location loc = player.getLocation();
        event.setCancelled(true);
        player.closeInventory();
        WandType type = WandType.valueOf(typeStr);
        startRitual(player, loc, type);
    }

    private void startRitual(Player crafter, Location loc, WandType type) {
        Location ritualLoc = loc.clone().add(0, 0, 0);
        new BukkitRunnable() {
            int ticks = 0;
            ArmorStand stand;
            public void run() {
                if (ticks == 0) {
                    ritualLoc.getBlock().setType(Material.COBBLESTONE);
                    stand = ritualLoc.getWorld().spawn(ritualLoc.clone().add(0.5, 1, 0.5), ArmorStand.class);
                    stand.setInvisible(true);
                    stand.getEquipment().setItemInMainHand(type.getItem(plugin));
                }
                if (ticks < 40) {
                    ritualLoc.add(0, 0.05, 0);
                    ritualLoc.getBlock().setType(Material.COBBLESTONE);
                    stand.teleport(ritualLoc.clone().add(0.5, 1, 0.5));
                }
                stand.setRotation(stand.getLocation().getYaw() + 10, 0);
                ritualLoc.getWorld().spawnParticle(Particle.PORTAL, ritualLoc.clone().add(0.5, 1.5, 0.5), 5, 0.2, 0.2, 0.2, 0.1);
                if (ticks++ > 100) {
                    createBossBar(type, ritualLoc);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1L);
    }

    private void createBossBar(WandType type, Location loc) {
        BossBar bar = Bukkit.createBossBar("§6§lWAND RITUAL: §f" + type.getDisplayName() + " at " + loc.getBlockX() + ", " + loc.getBlockY(), BarColor.PURPLE, BarStyle.SOLID);
        Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
        new BukkitRunnable() {
            int time = 600; // 10 mins
            public void run() {
                if (time-- <= 0) {
                    loc.getWorld().dropItemNaturally(loc.add(0.5, 1, 0.5), type.getItem(plugin));
                    bar.removeAll();
                    this.cancel();
                }
                bar.setProgress(time / 600.0);
            }
        }.runTaskTimer(plugin, 0, 20L);
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
            executeAbility(p, type);
        }
    }

    private void shootLaser(Player p) {
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1f, 2f);
        RayTraceResult ray = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), 30, (e) -> !e.equals(p));
        if (ray != null && ray.getHitEntity() instanceof LivingEntity le) {
            le.damage(4.0, p);
            le.getWorld().spawnParticle(Particle.SONIC_BOOM, le.getLocation(), 1);
        }
    }

    private void executeAbility(Player p, WandType type) {
        Location loc = p.getLocation();
        switch (type) {
            case FROST_AEGIS -> {
                for (int i = 0; i < 360; i += 10) {
                    double angle = Math.toRadians(i);
                    Location spike = loc.clone().add(Math.cos(angle) * 10, 0, Math.sin(angle) * 10);
                    spike.getWorld().spawnParticle(Particle.SNOWFLAKE, spike, 50, 0.5, 5, 0.5, 0.1);
                }
                new BukkitRunnable() {
                    int t = 0;
                    public void run() {
                        for (Entity e : p.getNearbyEntities(10, 5, 10)) {
                            if (e instanceof LivingEntity le) {
                                FallingBlock ice = le.getWorld().spawnFallingBlock(le.getLocation().add(0, 5, 0), Material.ICE.createBlockData());
                                ice.setDropItem(false);
                            }
                        }
                        if (t++ > 5) this.cancel();
                    }
                }.runTaskTimer(plugin, 0, 20L);
            }
            case DOMAIN_VOID -> {
                Entity target = null;
                for (Entity e : p.getNearbyEntities(10, 10, 10)) {
                    if (e instanceof Player) { target = e; break; }
                }
                if (target == null) return;
                Location domainLoc = new Location(p.getWorld(), 5000, 200, 5000);
                p.teleport(domainLoc);
                target.teleport(domainLoc.clone().add(5, 0, 0));
                activeDomains.put(p.getUniqueId(), type);
                LivingEntity finalTarget = (LivingEntity) target;
                new BukkitRunnable() {
                    int t = 0;
                    public void run() {
                        finalTarget.damage(1.0);
                        domainLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, domainLoc, 100, 10, 10, 10);
                        if (t++ > 200) {
                            activeDomains.remove(p.getUniqueId());
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20L);
            }
            case INFERNO_LANCE -> {
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 500, 5, 1, 5, 0.1);
                loc.getWorld().strikeLightningEffect(loc);
            }
        }
    }
}