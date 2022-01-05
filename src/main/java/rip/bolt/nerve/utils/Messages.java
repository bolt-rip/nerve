package rip.bolt.nerve.utils;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static rip.bolt.nerve.utils.Components.command;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.QueueUpdate.Action;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.privateserver.PrivateServerConfig;

public class Messages {

    public static final TextComponent PREFIX = text().append(colour(NamedTextColor.DARK_GRAY, "[")).append(bold(colour(NamedTextColor.YELLOW, "\u26A1")), colour(NamedTextColor.DARK_GRAY, "]")).append(colour(NamedTextColor.WHITE, " ")).build();
    public static final TextComponent DASH = colour(NamedTextColor.DARK_GRAY, " - ");

    @Inject
    private static PrivateServerConfig privateServerConfig;

    public static TextComponent privateServerStarted(String server) {
        return text().append(PREFIX).append(colour(NamedTextColor.GREEN, "Your private server has started up! Run ")).append(command(NamedTextColor.YELLOW, "server", server)).append(colour(NamedTextColor.GREEN, " to connect.")).build();
    }

    public static TextComponent rankedMatchReady(String server, String map) {
        return text().append(PREFIX).append(colour(NamedTextColor.GREEN, "Your ranked match on ")).append(colour(NamedTextColor.YELLOW, map)).append(colour(NamedTextColor.GREEN, " is starting soon! Run ")).append(command(NamedTextColor.YELLOW, "server", server)).append(colour(NamedTextColor.GREEN, " to connect.")).build();
    }

    public static TextComponent playerJoinLeaveQueue(String player, int inQueue, int queueSize, Action action, boolean targetIsReceiving) {
        return text().append(PREFIX).append(colour(targetIsReceiving ? NamedTextColor.GREEN : NamedTextColor.YELLOW, targetIsReceiving ? "You" : player)).append(colour(NamedTextColor.GREEN, (action == Action.JOIN ? " joined" : " left") + " the queue")).append(colour(NamedTextColor.DARK_GRAY, " | ")).append(colour(NamedTextColor.WHITE, String.valueOf(inQueue))).append(colour(NamedTextColor.DARK_GRAY, "/")).append(colour(NamedTextColor.GRAY, String.valueOf(queueSize))).build();
    }

    public static TextComponent vetoMessage() {
        return text().append(PREFIX).append(colour(NamedTextColor.GOLD, "Your match is starting. Pick the map you want to ")).append(bold(colour(NamedTextColor.RED, "veto"))).append(colour(NamedTextColor.GOLD, ":")).build();
    }

    public static TextComponent vetoOptions(List<String> maps) {
        TextComponent[] message = new TextComponent[maps.size() * 2];
        for (int i = 0; i < maps.size(); i++) {
            message[i * 2] = DASH;
            message[i * 2 + 1] = formatMapName(maps.get(i));
        }

        message[0] = PREFIX;
        TextComponent.Builder builder = text();
        for (TextComponent component : message)
            builder = builder.append(component);
        return builder.build();
    }

    public static TextComponent formatMapName(String map) {
        return command(NamedTextColor.YELLOW, text(map), "bolt", "veto", map);
    }

    public static TextComponent mapNotFound(String map) {
        return text().append(colour(NamedTextColor.RED, "Map ")).append(colour(NamedTextColor.RED, map)).append(colour(NamedTextColor.RED, " not found.")).build();
    }

    public static TextComponent vetoed(String map, @Nullable String teammateName, boolean vetoedBefore) {
        TextComponent.Builder builder = text().append(PREFIX);

        String displayName = teammateName;
        String verb = "has";
        String determiner = "their";

        if (teammateName == null) {
            displayName = "You";
            verb = "have";
            determiner = "your";
        }

        String sentence;
        if (vetoedBefore)
            sentence = String.format(" %s changed %s veto to ", verb, determiner);
        else
            sentence = String.format(" %s veteoed ", verb);

        builder.append(colour(NamedTextColor.YELLOW, displayName)).append(colour(NamedTextColor.GOLD, sentence));
        return builder.append(colour(NamedTextColor.YELLOW, map)).append(colour(NamedTextColor.GOLD, "!")).build();
    }

    public static TextComponent mapDecided(String map) {
        return text().append(PREFIX).append(colour(NamedTextColor.GOLD, "You will be playing on ")).append(colour(NamedTextColor.YELLOW, map)).append(colour(NamedTextColor.GOLD, "!")).build();
    }

    public static TextComponent formatMatchHeader(Match match) {
        return text().append(strikethrough(colour(NamedTextColor.DARK_GRAY, "     "))).append(colour(NamedTextColor.YELLOW, " " + match.getId() + " (")).append(colour(NamedTextColor.AQUA, match.getStatus().toString())).append(colour(NamedTextColor.YELLOW, ") ")).append(strikethrough(colour(NamedTextColor.DARK_GRAY, "     "))).build();
    }

    public static TextComponent formatMatchMap(Match match) {
        return colour(NamedTextColor.YELLOW, "Map: " + match.getMap());
    }

    public static TextComponent formatMatchServer(Match match) {
        return colour(NamedTextColor.YELLOW, "Server: " + match.getServer());
    }

    public static TextComponent formatTeam(Team team) {
        TextComponent[] message = new TextComponent[team.getPlayers().size() * 2];
        for (int i = 0; i < team.getPlayers().size(); i++) {
            message[i * 2] = colour(NamedTextColor.WHITE, ", ");
            message[i * 2 + 1] = colour(NamedTextColor.AQUA, team.getPlayers().get(i).getUsername());
        }
        message[0] = empty();
        TextComponent.Builder builder = text();
        for (TextComponent component : message)
            builder = builder.append(component);
        return builder.build();
    }

    public static TextComponent removeMatch(Match match) {
        return colour(NamedTextColor.YELLOW, "Match " + match.getId() + " has been removed!");
    }

    public static TextComponent formatStaffOnline(String server, List<String> staff) {
        TextComponent[] message = new TextComponent[Math.max(staff.size(), 1) * 2];

        for (int i = 0; i < staff.size(); i++) {
            message[i * 2] = colour(NamedTextColor.WHITE, ", ");
            message[i * 2 + 1] = colour(NamedTextColor.AQUA, staff.get(i));
        }
        message[0] = formatServerName(server);
        if (staff.size() == 0)
            message[1] = colour(NamedTextColor.RED, "No staff online.");

        TextComponent.Builder builder = text();
        for (TextComponent component : message)
            builder = builder.append(component);
        return builder.build();
    }

    public static TextComponent formatServerName(String server) {
        return text().append(colour(NamedTextColor.WHITE, "[")).append(colour(NamedTextColor.GOLD, server)).append(colour(NamedTextColor.WHITE, "] ")).build();
    }

    public static TextComponent noPermsPrivateServer() {
        return text().append(LegacyComponentSerializer.legacyAmpersand().deserialize(privateServerConfig.no_perms_message())).append(link(privateServerConfig.no_perms_link())).build();
    }

    public static TextComponent colour(NamedTextColor colour, String text) {
        return colour(colour, text(text));
    }

    public static TextComponent colour(NamedTextColor colour, TextComponent text) {
        return text.color(colour);
    }

    public static <T extends Component> T bold(T text) {
        text.decoration(TextDecoration.BOLD, true);

        return text;
    }

    public static <T extends Component> T strikethrough(T text) {
        text.decoration(TextDecoration.STRIKETHROUGH, true);

        return text;
    }

    public static <T extends Component> T italic(T text) {
        text.decoration(TextDecoration.ITALIC, true);

        return text;
    }

    public static TextComponent link(String url) {
        TextComponent link = Component.text(url);
        link = link.color(NamedTextColor.BLUE);
        link = link.decoration(TextDecoration.UNDERLINED, true);
        link = link.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, url));

        return link;
    }

    public static Component[] indent(int amount, TextComponent... components) {
        Component[] message = new Component[components.length + 1];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < amount; i++)
            builder.append(" ");
        System.arraycopy(components, 0, message, 1, components.length);
        message[0] = Component.text(builder.toString());

        return message;
    }

}
