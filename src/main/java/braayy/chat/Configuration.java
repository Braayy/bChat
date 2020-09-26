package braayy.chat;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class Configuration {

    public static String chatFormatCommon;

    public static String chatFormatGlobal;
    public static String chatFormatLocal;

    public static ChatType chatType;

    public static int localRadius;

    public static int globalPrice;

    public static int antiSpawnRate;

    public static String noMoneyMessage;
    public static String noPermissionMessage;
    public static String enableChatMessage;
    public static String disableChatMessage;
    public static String disabledChatMessage;
    public static String antiSpawnMessage;

    public static List<String> blockedWords;

    public static void load(Plugin plugin) {
        chatFormatCommon = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat-format.common"));

        chatFormatGlobal = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat-format.regional.global"));
        chatFormatLocal = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat-format.regional.local"));

        chatType = ChatType.valueOf(plugin.getConfig().getString("chat-type"));

        localRadius = plugin.getConfig().getInt("local.radius");

        globalPrice = plugin.getConfig().getInt("global.price");

        antiSpawnRate = plugin.getConfig().getInt("anti-spam-rate");

        noMoneyMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-money"));
        noPermissionMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permission"));
        enableChatMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.enable-chat"));
        disableChatMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.disable-chat"));
        disabledChatMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.disabled-chat"));
        antiSpawnMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.anti-spam"));

        blockedWords = plugin.getConfig().getStringList("blocked-words");
    }
}