package com.tcenema.magicwands;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface WandEffect {
    void execute(Player player, MagicWands plugin);
}