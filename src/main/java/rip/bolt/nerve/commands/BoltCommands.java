package rip.bolt.nerve.commands;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.event.RedisConnectEvent;
import rip.bolt.nerve.managers.MatchRegistry;
import rip.bolt.nerve.managers.VetoManager;
import rip.bolt.nerve.utils.Components;
import rip.bolt.nerve.utils.Messages;

public class BoltCommands {

    private static MatchRegistry registry = NervePlugin.getInstance().getMatchRegistry();
    private static VetoManager vetoManager = NervePlugin.getInstance().getVetoManager();

    public static class BoltParentCommand {

        @Command(aliases = { "bolt" }, desc = "Bolt management commands")
        @NestedCommand({ BoltCommands.class })
        public static void bolt(final CommandContext args, CommandSender sender) throws CommandException {

        }

    }

    @Command(aliases = { "veto" }, desc = "Veto a map", min = 1)
    public static void veto(final CommandContext cmd, CommandSender sender) throws CommandException {
        if (!(sender instanceof ProxiedPlayer))
            throw new CommandException("Only players can run this command!");
        ProxiedPlayer player = (ProxiedPlayer) sender;

        String map = Components.toSpace(cmd.getJoinedStrings(0));
        Match match = registry.getPlayerMatch(player);
        if (match == null)
            throw new CommandException("You can not veto at this time!");

        NervePlugin.async(() -> {
            vetoManager.vetoMap(player, match, map);
        });
    }

    @Command(aliases = { "active" }, desc = "View loaded matches", max = 0)
    @CommandPermissions("nerve.staff")
    public static void active(final CommandContext cmd, CommandSender sender) throws CommandException {
        if (registry.getLatestMatches().size() == 0)
            throw new CommandException("No matches are loaded.");

        for (Match match : registry.getLatestMatches().values()) {
            sender.sendMessage(Messages.formatMatchHeader(match));
            if (match.getMap() != null)
                sender.sendMessage(Messages.formatMatchMap(match));
            sender.sendMessage(Messages.formatMatchServer(match));

            for (Team team : match.getTeams()) {
                sender.sendMessage(Messages.colour(ChatColor.YELLOW, team.getName() + ":"));
                sender.sendMessage(Messages.formatTeam(team));
            }
        }
    }

    @Command(aliases = { "remove" }, desc = "Remove a match from the MatchRegistry", usage = "<server|match-id>", min = 1, max = 1)
    @CommandPermissions("nerve.staff")
    public static void remove(final CommandContext cmd, CommandSender sender) throws CommandException {
        String input = cmd.getString(0);
        Match target = registry.getMatchFromServerName(input);
        if (target == null)
            target = registry.getMatchFromId(input);

        if (target == null)
            throw new CommandException("Match not found!");

        registry.removeMatch(target);
        sender.sendMessage(Messages.removeMatch(target));
    }

    @Command(aliases = { "sync" }, desc = "Sync loaded matches with API", max = 0)
    @CommandPermissions("nerve.staff")
    public static void sync(final CommandContext cmd, CommandSender sender) throws CommandException {
        registry.onRedisConnect(new RedisConnectEvent()); // yuck
        sender.sendMessage(Messages.colour(ChatColor.YELLOW, "Sync queued."));
    }

    @Command(aliases = { "staff", "os" }, desc = "Lists online staff on ranked servers", max = 0)
    @CommandPermissions("nerve.staff")
    public static void staff(final CommandContext cmd, CommandSender sender) throws CommandException {
        NervePlugin.async(() -> {
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                if (!(server.getName().toLowerCase().startsWith("ranked-") || server.getName().equals("lobby")))
                    continue;

                List<String> onlineStaff = new ArrayList<String>();
                for (ProxiedPlayer player : server.getPlayers())
                    if (player.hasPermission("nerve.staff"))
                        onlineStaff.add(player.getName());

                sender.sendMessage(Messages.formatStaffOnline(server.getName(), onlineStaff));
            }
        });
    }

}
