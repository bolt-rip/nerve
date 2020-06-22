package rip.bolt.nerve.utils;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NameUtils {

    public static String formatName(CommandSender sender) {
        String flair = sender.hasPermission("nerve.staff") ? ChatColor.GOLD + "\u2756" : "";

        return flair + ChatColor.DARK_AQUA + sender.getName();
    }

    public static String formatName(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player != null)
            return formatName(player);

        return ChatColor.DARK_AQUA + NameFetcher.getNameFromUUID(uuid);
    }

}
