package rip.bolt.nerve.utils;

import static rip.bolt.nerve.utils.Components.command;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Messages {

    private static final TextComponent PREFIX = new TextComponent(colour(ChatColor.DARK_GRAY, "["), bold(colour(ChatColor.YELLOW, "\u26A1")), colour(ChatColor.DARK_GRAY, "]"), colour(ChatColor.RESET, " "));
    private static final TextComponent DASH = colour(ChatColor.DARK_GRAY, " - ");

    public static BaseComponent[] privateServerStarted(String server) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GREEN, "Your private server has started up! Run "), command(ChatColor.YELLOW, "server", server), colour(ChatColor.GREEN, " to connect.") };
    }

    public static BaseComponent[] rankedMatchReady(String server, String map) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GREEN, "Your ranked match on "), colour(ChatColor.YELLOW, map), colour(ChatColor.GREEN, " is starting soon! Run "), command(ChatColor.YELLOW, "server", server), colour(ChatColor.GREEN, " to connect.") };
    }

    public static BaseComponent[] playerJoinLeaveQueue(String player, int inQueue, int queueSize, boolean joined, boolean targetIsReceiving) {
        return new BaseComponent[] { PREFIX, colour(targetIsReceiving ? ChatColor.GREEN : ChatColor.YELLOW, targetIsReceiving ? "You" : player), colour(ChatColor.GREEN, (joined ? " joined" : " left") + " the queue"), colour(ChatColor.DARK_GRAY, " | "), colour(ChatColor.WHITE, String.valueOf(inQueue)), colour(ChatColor.DARK_GRAY, "/"), colour(ChatColor.GRAY, String.valueOf(queueSize)) };
    }

    public static BaseComponent[] vetoMessage() {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GOLD, "Your match is starting. Pick the map you want to "), bold(colour(ChatColor.RED, "veto")), colour(ChatColor.GOLD, ":") };
    }

    public static BaseComponent[] vetoOptions(List<String> maps) {
        BaseComponent[] message = new BaseComponent[maps.size() * 2];
        for (int i = 0; i < maps.size(); i++) {
            message[i * 2] = DASH;
            message[i * 2 + 1] = formatMapName(maps.get(i));
        }

        message[0] = PREFIX;
        return message;
    }

    public static BaseComponent formatMapName(String map) {
        return command(ChatColor.YELLOW, new TextComponent(map), "bolt", "veto", map);
    }

    public static BaseComponent[] vetoed(String map) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GOLD, "You have vetoed "), colour(ChatColor.YELLOW, map), colour(ChatColor.GOLD, "!") };
    }

    public static TextComponent colour(ChatColor colour, String text) {
        return colour(colour, new TextComponent(text));
    }

    public static TextComponent colour(ChatColor colour, TextComponent text) {
        text.setColor(colour);

        return text;
    }

    public static <T extends BaseComponent> T bold(T text) {
        text.setBold(true);

        return text;
    }

}
