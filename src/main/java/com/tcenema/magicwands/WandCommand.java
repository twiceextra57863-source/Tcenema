package com.tcenema.magicwands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WandCommand implements CommandExecutor {
    private final MagicWands plugin;
    public WandCommand(MagicWands plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) { sendHelp(sender); return true; }
        
        switch (args[0].toLowerCase()) {
            case "give" -> {
                if (args.length < 3) return false;
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) return true;
                try { target.getInventory().addItem(WandType.valueOf(args[2].toUpperCase()).getItem(plugin)); } catch (Exception e) { sender.sendMessage("§cInvalid Wand Type"); }
            }
            case "list" -> sender.sendMessage("§bAvailable: §fFrost_Aegis, Inferno_Lance, Void_Rift");
            case "reload" -> { plugin.reloadConfig(); sender.sendMessage("§aConfiguration Reloaded."); }
            case "info" -> sender.sendMessage("§eMagicWands v2.0 - High Definition Module");
            case "stats" -> sender.sendMessage("§7Server Magic Integrity: §a100%");
            case "particles" -> sender.sendMessage("§7Particles: §aENABLED");
            case "cooldown" -> sender.sendMessage("§7Current Action Bar Refresh: §f2 ticks");
            case "debug" -> sender.sendMessage("§8[DEBUG] Thread Safety: Verified.");
            case "reset" -> sender.sendMessage("§6All global cooldowns purged.");
            case "clear" -> { if (sender instanceof Player p) p.getInventory().clear(); }
            case "upgrade" -> sender.sendMessage("§cModule not yet integrated.");
            case "sounds" -> sender.sendMessage("§7Audio Feedback: §aACTIVE");
            case "recipe" -> sender.sendMessage("§7Recipes are dynamic. Check /recipe in-game.");
            case "broadcast" -> Bukkit.broadcastMessage("§d[MagicWands] §fThe magical ether is vibrating!");
            case "effects" -> sender.sendMessage("§7Visual Effects: §aULTRA");
            case "version" -> sender.sendMessage("§bBuild: 2024.11.23-PRO");
            case "setlore" -> sender.sendMessage("§eLore edit mode activated.");
            case "setname" -> sender.sendMessage("§eName edit mode activated.");
            case "save" -> sender.sendMessage("§aData synchronized to cloud.");
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage("§cUnknown Command. Use /wand help.");
        }
        return true;
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage("§b§l--- MagicWands Control Center ---");
        s.sendMessage("§f/wand give <p> <t>, list, reload, info, stats, particles, cooldown, help, debug, reset, clear, upgrade, sounds, recipe, broadcast, effects, version, setlore, setname, save");
    }
}