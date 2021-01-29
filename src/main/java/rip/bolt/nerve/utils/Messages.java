package rip.bolt.nerve.utils;

import static rip.bolt.nerve.utils.Components.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Messages {

    private static final TextComponent PREFIX = new TextComponent(colour(ChatColor.DARK_GRAY, "["), bold(colour(ChatColor.YELLOW, "\u26A1")), colour(ChatColor.DARK_GRAY, "]"), colour(ChatColor.RESET, " "));

    public static BaseComponent[] privateServerStarted(String server) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GREEN, "Your private server has started up! Run "), command(ChatColor.YELLOW, "server", server), colour(ChatColor.GREEN, " to connect.") };
    }

    public static BaseComponent[] rankedMatchReady(String server) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GREEN, "Your ranked match on server "), colour(ChatColor.YELLOW, server), colour(ChatColor.GREEN, " is starting soon! Run "), command(ChatColor.YELLOW, "server", server), colour(ChatColor.GREEN, " to connect.") };
    }

    public static BaseComponent[] playerJoinLeaveQueue(String player, int inQueue, int queueSize, boolean joined, boolean targetIsReceiving) {
        return new BaseComponent[] { PREFIX, colour(targetIsReceiving ? ChatColor.GREEN : ChatColor.YELLOW, targetIsReceiving ? "You" : player), colour(ChatColor.GREEN, (joined ? " joined" : " left") + " the queue"), colour(ChatColor.DARK_GRAY, " | "), colour(ChatColor.WHITE, String.valueOf(inQueue)), colour(ChatColor.DARK_GRAY, "/"), colour(ChatColor.GRAY, String.valueOf(queueSize)) };
    }

    public static TextComponent colour(ChatColor colour, String text) {
        return colour(colour, new TextComponent(text));
    }

    public static TextComponent colour(ChatColor colour, TextComponent text) {
        text.setColor(colour);

        return text;
    }

    public static TextComponent bold(TextComponent text) {
        text.setBold(true);

        return text;
    }

}
