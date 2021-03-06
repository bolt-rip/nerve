package rip.bolt.nerve.utils;

import static rip.bolt.nerve.utils.Components.command;

import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.QueueUpdate.Action;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.config.AppData;

public class Messages {

    public static final TextComponent PREFIX = new TextComponent(colour(ChatColor.DARK_GRAY, "["), bold(colour(ChatColor.YELLOW, "\u26A1")), colour(ChatColor.DARK_GRAY, "]"), colour(ChatColor.RESET, " "));
    public static final TextComponent DASH = colour(ChatColor.DARK_GRAY, " - ");

    public static BaseComponent[] privateServerStarted(String server) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GREEN, "Your private server has started up! Run "), command(ChatColor.YELLOW, "server", server), colour(ChatColor.GREEN, " to connect.") };
    }

    public static BaseComponent[] rankedMatchReady(String server, String map) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GREEN, "Your ranked match on "), colour(ChatColor.YELLOW, map), colour(ChatColor.GREEN, " is starting soon! Run "), command(ChatColor.YELLOW, "server", server), colour(ChatColor.GREEN, " to connect.") };
    }

    public static BaseComponent[] playerJoinLeaveQueue(String player, int inQueue, int queueSize, Action action, boolean targetIsReceiving) {
        return new BaseComponent[] { PREFIX, colour(targetIsReceiving ? ChatColor.GREEN : ChatColor.YELLOW, targetIsReceiving ? "You" : player), colour(ChatColor.GREEN, (action == Action.JOIN ? " joined" : " left") + " the queue"), colour(ChatColor.DARK_GRAY, " | "), colour(ChatColor.WHITE, String.valueOf(inQueue)), colour(ChatColor.DARK_GRAY, "/"), colour(ChatColor.GRAY, String.valueOf(queueSize)) };
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

    public static BaseComponent[] mapNotFound(String map) {
        return new BaseComponent[] { colour(ChatColor.RED, "Map "), colour(ChatColor.RED, map), colour(ChatColor.RED, " not found.") };
    }

    public static BaseComponent[] vetoed(String map, boolean vetoedBefore) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GOLD, vetoedBefore ? "You have changed your veto to " : "You have vetoed "), colour(ChatColor.YELLOW, map), colour(ChatColor.GOLD, "!") };
    }

    public static BaseComponent[] mapDecided(String map) {
        return new BaseComponent[] { PREFIX, colour(ChatColor.GOLD, "You will be playing on "), colour(ChatColor.YELLOW, map), colour(ChatColor.GOLD, "!") };
    }

    public static BaseComponent[] formatMatchHeader(Match match) {
        return new BaseComponent[] { strikethrough(colour(ChatColor.DARK_GRAY, "     ")), colour(ChatColor.YELLOW, " " + match.getId() + " ("), colour(ChatColor.AQUA, match.getStatus().toString()), colour(ChatColor.YELLOW, ") "), strikethrough(colour(ChatColor.DARK_GRAY, "     ")) };
    }

    public static BaseComponent[] formatMatchMap(Match match) {
        return new BaseComponent[] { colour(ChatColor.YELLOW, "Map: " + match.getMap()) };
    }

    public static BaseComponent[] formatMatchServer(Match match) {
        return new BaseComponent[] { colour(ChatColor.YELLOW, "Server: " + match.getServer()) };
    }

    public static BaseComponent[] formatTeam(Team team) {
        BaseComponent[] message = new BaseComponent[team.getPlayers().size() * 2];
        for (int i = 0; i < team.getPlayers().size(); i++) {
            message[i * 2] = colour(ChatColor.WHITE, ", ");
            message[i * 2 + 1] = colour(ChatColor.AQUA, team.getPlayers().get(i).getUsername());
        }
        message[0] = new TextComponent();

        return message;
    }

    public static BaseComponent[] removeMatch(Match match) {
        return new BaseComponent[] { colour(ChatColor.YELLOW, "Match " + match.getId() + " has been removed!") };
    }

    public static BaseComponent[] formatStaffOnline(String server, List<String> staff) {
        BaseComponent[] message = new BaseComponent[Math.max(staff.size(), 1) * 2];

        for (int i = 0; i < staff.size(); i++) {
            message[i * 2] = colour(ChatColor.WHITE, ", ");
            message[i * 2 + 1] = colour(ChatColor.AQUA, staff.get(i));
        }
        message[0] = formatServerName(server);
        if (staff.size() == 0)
            message[1] = colour(ChatColor.RED, "No staff online.");

        return message;
    }

    public static TextComponent formatServerName(String server) {
        return new TextComponent(new BaseComponent[] { colour(ChatColor.WHITE, "["), colour(ChatColor.GOLD, server), colour(ChatColor.WHITE, "] ") });
    }

    public static BaseComponent[] noPermsPrivateServer() {
        return new BaseComponent[] { new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', AppData.PrivateServers.getNoPermsMessage()))), link(AppData.PrivateServers.getNoPermsLink()) };
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

    public static <T extends BaseComponent> T strikethrough(T text) {
        text.setStrikethrough(true);

        return text;
    }

    public static <T extends BaseComponent> T italic(T text) {
        text.setItalic(true);

        return text;
    }

    public static TextComponent link(String url) {
        TextComponent link = new TextComponent(url);
        link.setColor(ChatColor.BLUE);
        link.setUnderlined(true);
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

        return link;
    }

    public static BaseComponent[] indent(int amount, TextComponent... components) {
        BaseComponent[] message = new BaseComponent[components.length + 1];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < amount; i++)
            builder.append(" ");
        System.arraycopy(components, 0, message, 1, components.length);
        message[0] = new TextComponent(builder.toString());

        return message;
    }

}
