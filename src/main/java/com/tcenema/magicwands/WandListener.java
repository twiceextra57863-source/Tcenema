package com.tcenema.magicwands;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WandListener implements Listener {
    private final MagicWands plugin;
    private final WandEffectManager effectManager;
    private final Map<UUID, Map<WandType, Long>> cooldowns = new HashMap<>();
    
    public static boolean particlesEnabled = true;

    public WandListener(MagicWands plugin) {
        this.plugin = plugin;
        this.effectManager = new WandEffectManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        String typeStr = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "wand_type"), PersistentDataType.STRING);
        if (typeStr == null) return;

        WandType type = WandType.valueOf(typeStr);
        if (event.getAction().name().contains("RIGHT")) {
            if (isCooledDown(p, type)) {
                effectManager.handle(type, p, plugin);
                setCooldown(p, type);
            } else {
                p.sendMessage("§cCooldown active!");
            }
        }
    }

    private boolean isCooledDown(Player p, WandType type) {
        return !cooldowns.containsKey(p.getUniqueId()) || !cooldowns.get(p.getUniqueId()).containsKey(type) || 
               System.currentTimeMillis() > cooldowns.get(p.getUniqueId()).get(type);
    }

    private void setCooldown(Player p, WandType type) {
        cooldowns.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>()).put(type, System.currentTimeMillis() + (type.getCooldown() * 1000L));
    }
}