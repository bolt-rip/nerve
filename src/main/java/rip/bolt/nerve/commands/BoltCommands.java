package rip.bolt.nerve.commands;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.event.RedisConnectEvent;
import rip.bolt.nerve.inject.commands.Commands;
import rip.bolt.nerve.match.MatchRegistry;
import rip.bolt.nerve.match.listeners.VetoManager;
import rip.bolt.nerve.utils.Components;
import rip.bolt.nerve.utils.Executor;
import rip.bolt.nerve.utils.Messages;

public class BoltCommands implements Commands {

    private Executor executor;
    private ProxyServer server;
    private EventManager events;

    private MatchRegistry registry;
    private VetoManager vetoManager;

    @Inject
    public BoltCommands(Executor executor, ProxyServer server, EventManager events, MatchRegistry registry, VetoManager vetoManager) {
        this.executor = executor;
        this.server = server;
        this.events = events;

        this.registry = registry;
        this.vetoManager = vetoManager;
    }

    public static class BoltParentCommand implements Commands {

        @Command(aliases = { "bolt" }, desc = "Bolt management commands")
        @NestedCommand({ BoltCommands.class })
        public void bolt(final CommandContext args, CommandSource sender) throws CommandException {

        }

    }

    @Command(aliases = { "veto" }, desc = "Veto a map", usage = "<map>", min = 1)
    public void veto(final CommandContext cmd, CommandSource sender) throws CommandException {
        if (!(sender instanceof Player))
            throw new CommandException("Only players can run this command!");
        Player player = (Player) sender;

        String map = Components.toSpace(cmd.getJoinedStrings(0));
        Match match = registry.getPlayerMatch(player);
        if (match == null)
            throw new CommandException("You can not veto at this time!");

        executor.async(() -> {
            vetoManager.vetoMap(player, match, map);
        });
    }

    @Command(aliases = { "active" }, desc = "View loaded matches", max = 0)
    @CommandPermissions("nerve.staff")
    public void active(final CommandContext cmd, CommandSource sender) throws CommandException {
        if (registry.getLatestMatches().size() == 0)
            throw new CommandException("No matches are loaded.");

        for (Match match : registry.getLatestMatches().values()) {
            sender.sendMessage(Messages.formatMatchHeader(match));
            if (match.getMap() != null)
                sender.sendMessage(Messages.formatMatchMap(match));
            sender.sendMessage(Messages.formatMatchServer(match));

            for (Team team : match.getTeams()) {
                sender.sendMessage(Messages.colour(NamedTextColor.YELLOW, team.getName() + ":"));
                sender.sendMessage(Messages.formatTeam(team));
            }
        }
    }

    @Command(aliases = { "remove" }, desc = "Remove a match from the MatchRegistry", usage = "<server|match-id>", min = 1, max = 1)
    @CommandPermissions("nerve.staff")
    public void remove(final CommandContext cmd, CommandSource sender) throws CommandException {
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
    public void sync(final CommandContext cmd, CommandSource sender) throws CommandException {
        events.fire(new RedisConnectEvent()); // yuck
        sender.sendMessage(Component.text("Sync queued.").color(NamedTextColor.YELLOW));
    }

    @Command(aliases = { "staff", "os" }, desc = "Lists online staff on ranked servers", max = 0)
    @CommandPermissions("nerve.staff")
    public void staff(final CommandContext cmd, CommandSource sender) throws CommandException {
        executor.async(() -> {
            for (RegisteredServer server : this.server.getAllServers()) {
                if (!(server.getServerInfo().getName().toLowerCase().startsWith("ranked-") || server.getServerInfo().getName().equals("lobby")))
                    continue;

                List<String> onlineStaff = new ArrayList<String>();
                for (Player player : server.getPlayersConnected())
                    if (player.hasPermission("nerve.staff"))
                        onlineStaff.add(player.getUsername());

                sender.sendMessage(Messages.formatStaffOnline(server.getServerInfo().getName(), onlineStaff));
            }
        });
    }

}
