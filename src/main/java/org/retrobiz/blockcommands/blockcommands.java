package org.RetroBiz.blockcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class blockcommands extends JavaPlugin implements Listener {

    private static final String BLOCKED_PLAYER = "VovchikZOVchik";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("BlockCommands plugin is enable.");
    }

    @Override
    public void onDisable() {
        getLogger().info("BlockCommands plugin is disabled.");
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equalsIgnoreCase(BLOCKED_PLAYER)) {
            event.setCancelled(true);
            player.sendMessage("§c а тебе низя юзать комманды иди делать огэ!");
        }
    }

    @EventHandler
    public void onCommand(ServerCommandEvent event) {
        if (event.getSender() instanceof ConsoleCommandSender) {
            event.getSender().sendMessage("§c консоль ушла делать огэ и тебе пора!");
            event.setCancelled(true);
        }
    }
}