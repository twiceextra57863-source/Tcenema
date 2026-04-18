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
                if (target == null) { sender.sendMessage("§cPlayer not found."); return true; }
                try {
                    WandType type = WandType.valueOf(args[2].toUpperCase());
                    target.getInventory().addItem(type.getItem(plugin));
                    sender.sendMessage("§aGave " + type.getDisplayName() + " to " + target.getName());
                } catch (IllegalArgumentException e) { sender.sendMessage("§cInvalid Wand Type: " + args[2]); }
            }
            case "list" -> sender.sendMessage("§bAvailable Wands: §fFrost_Aegis, Inferno_Lance, Domain_Void");
            case "particles" -> {
                WandListener.particlesEnabled = !WandListener.particlesEnabled;
                sender.sendMessage("§7Particles: " + (WandListener.particlesEnabled ? "§aENABLED" : "§cDISABLED"));
            }
            case "stats" -> {
                sender.sendMessage("§b--- Magic Stats ---");
                sender.sendMessage("§fActive Domains: §e" + "Checking..."); 
                sender.sendMessage("§fParticle Status: " + (WandListener.particlesEnabled ? "§aOnline" : "§cOffline"));
            }
            case "reload" -> { plugin.reloadConfig(); sender.sendMessage("§aConfiguration Reloaded."); }
            case "help" -> sendHelp(sender);
            default -> sender.sendMessage("§cUnknown Command. Use /wand help.");
        }
        return true;
    }

    private void sendHelp(CommandSender s) {
        s.sendMessage("§b§l--- MagicWands Control Center ---");
        s.sendMessage("§f/wand give <player> <type> §7- Give a wand");
        s.sendMessage("§f/wand list §7- List types");
        s.sendMessage("§f/wand particles §7- Toggle effects");
        s.sendMessage("§f/wand stats §7- Show system health");
        s.sendMessage("§f/wand reload §7- Reload config");
    }
}