package com.tcenema.magicwands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class WandListener implements Listener {
    private final MagicWands plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public WandListener(MagicWands plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        String typeStr = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "wand_type"), PersistentDataType.STRING);
        if (typeStr == null) return;

        Player player = event.getPlayer();
        WandType type = WandType.valueOf(typeStr);

        if (isOnCooldown(player)) {
            sendCooldownMessage(player);
            return;
        }

        executeAbility(player, type);
        startCooldown(player, type.getCooldown());
    }

    private boolean isOnCooldown(Player p) {
        return cooldowns.containsKey(p.getUniqueId()) && cooldowns.get(p.getUniqueId()) > System.currentTimeMillis();
    }

    private void startCooldown(Player p, int seconds) {
        long end = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(p.getUniqueId(), end);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!p.isOnline() || System.currentTimeMillis() >= end) {
                    p.sendActionBar(Component.text("§a§lWAND READY"));
                    this.cancel();
                    return;
                }
                long remaining = end - System.currentTimeMillis();
                double percent = (double) remaining / (seconds * 1000.0);
                StringBuilder bar = new StringBuilder("§c[");
                int bars = (int) (20 * percent);
                for (int i = 0; i < 20; i++) bar.append(i < bars ? "█" : "§7░");
                bar.append("§c] §f" + (remaining / 1000 + 1) + "s");
                p.sendActionBar(Component.text(bar.toString()));
            }
        }.runTaskTimer(plugin, 0, 2L);
    }

    private void sendCooldownMessage(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
    }

    private void executeAbility(Player p, WandType type) {
        Location loc = p.getLocation();
        switch (type) {
            case FROST_AEGIS -> {
                p.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 200, 4, 1, 4, 0.05);
                p.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 1f, 0.5f);
                for (Entity e : p.getNearbyEntities(6, 3, 6)) {
                    if (e instanceof LivingEntity le && !le.equals(p)) {
                        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 5));
                        le.getWorld().spawnParticle(Particle.SNOWFLAKE, le.getLocation(), 20);
                    }
                }
            }
            case INFERNO_LANCE -> {
                Vector dir = loc.getDirection();
                new BukkitRunnable() {
                    int step = 0;
                    public void run() {
                        Location s = loc.clone().add(dir.clone().multiply(step));
                        p.getWorld().spawnParticle(Particle.FLAME, s, 10, 0.2, 0.2, 0.2, 0.1);
                        p.getWorld().spawnParticle(Particle.LAVA, s, 2);
                        for (Entity e : s.getNearbyEntities(1.5, 1.5, 1.5)) {
                            if (e instanceof LivingEntity le && !le.equals(p)) le.setFireTicks(100);
                        }
                        if (++step > 15) this.cancel();
                    }
                }.runTaskTimer(plugin, 0, 1L);
            }
            case VOID_RIFT -> {
                p.getWorld().spawnParticle(Particle.PORTAL, loc, 300, 3, 3, 3, 0.5);
                p.getWorld().playSound(loc, Sound.BLOCK_PORTAL_TRAVEL, 0.8f, 1.5f);
                for (Entity e : p.getNearbyEntities(12, 12, 12)) {
                    if (!e.equals(p)) e.setVelocity(loc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(1.2));
                }
            }
        }
    }
}