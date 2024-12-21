package org.RetroBiz.blockcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class blockcommands extends JavaPlugin implements Listener {

    public static boolean BlockConsole = false;
    public static List<String> BlockedPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("BlockCommands плагин включён!");
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("block").setExecutor(this);
        loadConfigData();
    }

    @Override
    public void onDisable() {
        getLogger().info("BlockCommands плагин выключен!");
        saveConfigData();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.startsWith("~block") && message.length() > 6) {
            String targetName = message.substring(6).trim();
            Player targetPlayer = Bukkit.getPlayer(targetName);
            if (targetPlayer != null) {
                if (BlockedPlayers.contains(targetPlayer.getName())) {
                    BlockedPlayers.remove(targetPlayer.getName());
                    player.sendMessage(targetPlayer.getName() + " был разблокирован.");
                } else {
                    BlockedPlayers.add(targetPlayer.getName());
                    player.sendMessage(targetPlayer.getName() + " был заблокирован.");
                }
                event.setCancelled(true);
            } else {
                player.sendMessage("Игрок с именем " + targetName + " не найден.");
            }
        }

        if (message.equals("~block console")) {
            BlockConsole = !BlockConsole;
            player.sendMessage("Блокировка команд консоли " + (BlockConsole ? "включена" : "выключена") + ".");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();

        if (BlockConsole && sender instanceof ConsoleCommandSender) {
            event.setCancelled(true);
            sender.sendMessage("Команды от консоли заблокированы.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        CommandSender sender = event.getPlayer();

        if (BlockedPlayers.contains(sender.getName())) {
            event.setCancelled(true);
            sender.sendMessage("Вы заблокированы и не можете выполнять команды.");
        }
    }

    private void loadConfigData() {
        saveDefaultConfig();
        BlockConsole = getConfig().getBoolean("blockConsole", false);
        BlockedPlayers = getConfig().getStringList("blockedPlayers");
    }

    private void saveConfigData() {
        getConfig().set("blockConsole", BlockConsole);
        getConfig().set("blockedPlayers", BlockedPlayers);
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("block")) {
            if (args.length == 1) {
                String target = args[0];

                if (target.equalsIgnoreCase("console")) {
                    BlockConsole = !BlockConsole;
                    sender.sendMessage("Блокировка команд консоли " + (BlockConsole ? "включена" : "выключена"));
                } else {
                    Player targetPlayer = Bukkit.getPlayer(target);
                    if (targetPlayer != null) {
                        if (BlockedPlayers.contains(targetPlayer.getName())) {
                            BlockedPlayers.remove(targetPlayer.getName());
                            sender.sendMessage(targetPlayer.getName() + " был разблокирован.");
                        } else {
                            BlockedPlayers.add(targetPlayer.getName());
                            sender.sendMessage(targetPlayer.getName() + " был заблокирован.");
                        }
                    } else {
                        sender.sendMessage("Игрок с именем " + target + " не найден.");
                    }
                }
            }
            return true;
        }

        return false;
    }
}
