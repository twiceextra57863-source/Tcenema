package com.tcenema.magicwands;

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
        if (checkCooldown(player)) return;

        try {
            WandType type = WandType.valueOf(typeStr);
            executeAbility(player, type);
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 5000);
        } catch (IllegalArgumentException e) {
            // Invalid wand type stored
        }
    }

    private boolean checkCooldown(Player p) {
        if (cooldowns.containsKey(p.getUniqueId()) && cooldowns.get(p.getUniqueId()) > System.currentTimeMillis()) {
            p.sendMessage(ChatColor.RED + "Recharging magic... Wait " + ((cooldowns.get(p.getUniqueId()) - System.currentTimeMillis()) / 1000) + "s");
            return true;
        }
        return false;
    }

    private void executeAbility(Player p, WandType type) {
        Location loc = p.getLocation();
        switch (type) {
            case FROST_AEGIS -> {
                p.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 100, 3, 1, 3, 0.1);
                p.getWorld().playSound(loc, Sound.BLOCK_GLASS_BREAK, 1f, 0.5f);
                for (Entity e : p.getNearbyEntities(5, 5, 5)) {
                    if (e instanceof LivingEntity le) le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 10));
                }
            }
            case INFERNO_LANCE -> {
                p.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
                Vector dir = p.getLocation().getDirection();
                for (int i = 1; i < 15; i++) {
                    Location step = loc.clone().add(dir.clone().multiply(i));
                    p.getWorld().spawnParticle(Particle.FLAME, step, 5, 0.1, 0.1, 0.1, 0.05);
                    for (Entity e : step.getNearbyEntities(1, 1, 1)) {
                        if (e instanceof LivingEntity le && !le.equals(p)) le.setFireTicks(100);
                    }
                }
            }
            case VOID_RIFT -> {
                p.getWorld().spawnParticle(Particle.PORTAL, loc, 200, 2, 2, 2, 0.5);
                p.getWorld().playSound(loc, Sound.BLOCK_PORTAL_TRAVEL, 0.5f, 2f);
                for (Entity e : p.getNearbyEntities(10, 10, 10)) {
                    if (!e.equals(p)) e.setVelocity(loc.toVector().subtract(e.getLocation().toVector()).normalize().multiply(1.5));
                }
            }
        }
    }
}