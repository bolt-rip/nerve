package rip.bolt.nerve.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class NameUtils {

    public static String formatName(CommandSender sender) {
        String flair = sender.hasPermission("nerve.staff") ? ChatColor.GOLD + "\u2756" : "";

        return flair + ChatColor.DARK_AQUA + sender.getName();
    }

}
