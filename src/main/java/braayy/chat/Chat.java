package braayy.chat;

import braayy.chat.command.SimpleCommand;
import braayy.chat.hook.VaultHook;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Chat extends JavaPlugin implements Listener {

    private static final String CHAT_PERMISSION = "bchat.chat";
    private static final String COLOR_PERMISSION = "bchat.color";

    public boolean chatEnabled = true;

    public final Map<UUID, Long> lastMessageMap;

    public Chat() {
        this.lastMessageMap = new HashMap<>();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        Configuration.load(this);

        this.getServer().getPluginManager().registerEvents(this, this);

        SimpleCommand.add((sender, args) -> {
            if (!sender.hasPermission(CHAT_PERMISSION)) {
                sender.sendMessage(Configuration.noPermissionMessage);

                return;
            }

            this.chatEnabled = !this.chatEnabled;

            sender.sendMessage(this.chatEnabled ? Configuration.enableChatMessage : Configuration.disableChatMessage);
        }, "chat");

        if (Configuration.chatType == ChatType.REGIONAL) {
            if (Configuration.globalPrice > 0 && !VaultHook.setupEconomy()) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Vault or economy plugin not found, disabling plugin!");
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "You've choose regional chat and priced global");

                this.getServer().getPluginManager().disablePlugin(this);

                return;
            }

            SimpleCommand.add((sender, args) -> {
                if (!(sender instanceof Player)) return;
                if (args.length <= 0) return;

                Player player = (Player) sender;

                if (this.isChatDisabled(player)) {
                    player.sendMessage(Configuration.disabledChatMessage);

                    return;
                }

                if (this.isTypingFast(player)) {
                    player.sendMessage(Configuration.antiSpawnMessage);

                    return;
                }

                if (Configuration.globalPrice > 0) {
                    EconomyResponse response = VaultHook.economy.withdrawPlayer(player, Configuration.globalPrice);

                    if (!response.transactionSuccess()) {
                        player.sendMessage(Configuration.noMoneyMessage);

                        return;
                    }
                }

                String message = String.join(" ", args);

                if (player.hasPermission(COLOR_PERMISSION)) {
                    message = ChatColor.translateAlternateColorCodes('&', message);
                }

                message = unCurseMessage(message);

                String format = Configuration.chatFormatGlobal
                        .replace("{player}", player.getName())
                        .replace("{message}", message);

                Bukkit.broadcastMessage(format);

                this.lastMessageMap.put(player.getUniqueId(), System.currentTimeMillis());
            }, "global", "g");
        }
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (this.isChatDisabled(player)) {
            event.setCancelled(true);

            player.sendMessage(Configuration.disabledChatMessage);

            return;
        }

        if (this.isTypingFast(player)) {
            event.setCancelled(true);

            player.sendMessage(Configuration.antiSpawnMessage);

            return;
        }

        if (Configuration.chatType == ChatType.GLOBAL) {
            String message = event.getMessage();

            if (player.hasPermission(COLOR_PERMISSION)) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            message = unCurseMessage(message);

            String format = Configuration.chatFormatCommon
                    .replace("{player}", player.getName())
                    .replace("{message}", message);

            event.setFormat(format);
        } else {
            event.setCancelled(true);

            String message = event.getMessage();

            if (player.hasPermission(COLOR_PERMISSION)) {
                message = ChatColor.translateAlternateColorCodes('&', message);
            }

            message = unCurseMessage(message);

            String format = Configuration.chatFormatLocal
                    .replace("{player}", player.getName())
                    .replace("{message}", message);

            for (Player oPlayer : player.getWorld().getPlayers()) {
                double distSq = oPlayer.getLocation().distanceSquared(player.getLocation());

                if (distSq <= Configuration.localRadius * Configuration.localRadius) {
                    oPlayer.sendMessage(format);
                }
            }
        }

        this.lastMessageMap.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private boolean isChatDisabled(Player player) {
        return !this.chatEnabled && !player.hasPermission(CHAT_PERMISSION);
    }

    private boolean isTypingFast(Player player) {
        long lastMessage = this.lastMessageMap.getOrDefault(player.getUniqueId(), 0L);

        return System.currentTimeMillis() - lastMessage < Configuration.antiSpawnRate * 1000;
    }

    private static String unCurseMessage(String message) {
        for (String blockedWord : Configuration.blockedWords) {
            message = message.replace(blockedWord, createUnCursedWord(blockedWord));
        }

        return message;
    }

    private static String createUnCursedWord(String blockedWord) {
        char[] chars = new char[blockedWord.length()];

        Arrays.fill(chars, '*');

        return new String(chars);
    }

}