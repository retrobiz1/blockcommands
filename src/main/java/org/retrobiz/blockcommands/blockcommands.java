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

    // Статические переменные для хранения состояния плагина
    public static boolean BlockConsole = false;
    public static List<String> BlockedPlayers = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("BlockCommands плагин включён!");
        getServer().getPluginManager().registerEvents(this, this);

        loadConfigData();  // Загружаем настройки из конфигурации
    }

    @Override
    public void onDisable() {
        getLogger().info("BlockCommands плагин выключен!");
        saveConfigData();  // Сохраняем настройки в конфиг
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Если игрок пишет команду блокировки
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
                event.setCancelled(true); // Отменяем событие, чтобы не передавать сообщение в чат
            } else {
                player.sendMessage("Игрок с именем " + targetName + " не найден.");
            }
        }

        // Если игрок пишет команду блокировки консоли
        if (message.equals("~block console")) {
            BlockConsole = !BlockConsole;
            player.sendMessage("Блокировка команд консоли " + (BlockConsole ? "включена" : "выключена") + ".");
            event.setCancelled(true); // Отменяем событие, чтобы не передавать сообщение в чат
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();

        // Если включена блокировка команд консоли
        if (BlockConsole && sender instanceof ConsoleCommandSender) {
            event.setCancelled(true); // Блокируем выполнение команды
            sender.sendMessage("Команды от консоли заблокированы.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        CommandSender sender = event.getPlayer();

        // Если игрок заблокирован
        if (BlockedPlayers.contains(sender.getName())) {
            event.setCancelled(true); // Блокируем выполнение команды
            sender.sendMessage("Вы заблокированы и не можете выполнять команды.");
        }
    }

    // Загрузка данных конфигурации
    private void loadConfigData() {
        saveDefaultConfig();
        BlockConsole = getConfig().getBoolean("blockConsole", false);
        BlockedPlayers = getConfig().getStringList("blockedPlayers");
    }

    // Сохранение данных в конфигурацию
    private void saveConfigData() {
        getConfig().set("blockConsole", BlockConsole);
        getConfig().set("blockedPlayers", BlockedPlayers);
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Обработка команды /block
        if (command.getName().equalsIgnoreCase("block")) {
            if (args.length == 1) {
                String target = args[0];

                if (target.equalsIgnoreCase("console")) {
                    // Переключаем блокировку консольных команд
                    BlockConsole = !BlockConsole;
                    sender.sendMessage("Блокировка команд консоли " + (BlockConsole ? "включена" : "выключена"));
                } else {
                    // Блокировка/разблокировка игрока
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
