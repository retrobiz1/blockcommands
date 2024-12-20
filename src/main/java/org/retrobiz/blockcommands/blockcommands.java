package org.RetroBiz.blockcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class blockcommands extends JavaPlugin implements Listener {

    public static Boolean BlockConsole = false;
    public static List<String> BlockedPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("Plugin enabled!");
        getServer().getPluginManager().registerEvents(this, this);

        loadConfigData();
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");

        saveConfigData();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player who = event.getPlayer();
        String message = event.getMessage();

        if (message.startsWith("~block") && message.length() > 6) {
            // Блокировка другого игрока через команду в чате
            String playerName = message.substring(6).trim();  // Получаем имя игрока
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                who.sendMessage("Admin is hearing your voice");

                if (BlockedPlayers.contains(player.getName())) {
                    BlockedPlayers.remove(player.getName()); // Разблокировать
                    who.sendMessage(player.getName() + " has been unblocked.");
                } else {
                    BlockedPlayers.add(player.getName()); // Заблокировать
                    who.sendMessage(player.getName() + " has been blocked.");
                }

                event.setCancelled(true); // Отменяем событие, чтобы не передавалось сообщение
            } else {
                who.sendMessage("Player " + playerName + " is not online.");
            }
        }

        if (message.startsWith("~block") && message.length() == 6) {
            BlockConsole = !BlockConsole;
            who.sendMessage("Admin is hearing your voice");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();

        if (BlockConsole && sender instanceof ConsoleCommandSender) {
            event.setCancelled(true);
            sender.sendMessage("Nope, you can't run this command.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        CommandSender sender = event.getPlayer();

        if (BlockedPlayers.contains(sender.getName())) {
            event.setCancelled(true);
            sender.sendMessage("You are blocked from sending commands.");
        }
    }

    private void loadConfigData() {
        saveDefaultConfig(); // Создаём конфиг, если его нет
        BlockConsole = getConfig().getBoolean("blockConsole", false);
        BlockedPlayers = getConfig().getStringList("blockedPlayers");
    }

    private void saveConfigData() {
        getConfig().set("blockConsole", BlockConsole);
        getConfig().set("blockedPlayers", BlockedPlayers);
        saveConfig();
    }
}
