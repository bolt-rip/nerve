package rip.bolt.nerve.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.CommandException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.APIManager;
import rip.bolt.nerve.api.MatchStatus;
import rip.bolt.nerve.api.definitions.BoltResponse;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.PoolInformation;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.api.definitions.Veto;
import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Sounds;

public class VetoManager implements MatchStatusListener {

    private MatchRegistry registry;
    private APIManager api;
    private Map<Match, PoolInformation> pools;

    public VetoManager(MatchRegistry registry, APIManager api) {
        this.registry = registry;
        this.api = api;
        this.pools = new HashMap<Match, PoolInformation>();
    }

    @Override
    public void playerJoin(ProxiedPlayer player, Match match) {
        if (match.getStatus() != MatchStatus.CREATED)
            return;

        PoolInformation information = pools.get(match);
        if (information == null)
            return; // they will be sent the vetoes in a moment

        ProxyServer.getInstance().getScheduler().schedule(NervePlugin.getInstance(), () -> {
            if (match.getStatus() == MatchStatus.CREATED && match.getMap() == null)
                sendVetoes(player, match, information);
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void matchStatusUpdate(Match match) {
        if (match.getStatus() == MatchStatus.LOADED)
            pools.remove(match);

        if (match.getStatus() != MatchStatus.CREATED || match.getMap() != null)
            return;

        NervePlugin.async(() -> {
            int queueSize = match.getQueueSize();
            PoolInformation information = api.getPoolInformation(queueSize);
            pools.put(match, information);

            for (Team team : match.getTeams()) {
                inner: for (User participant : team.getPlayers()) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(participant.getUUID());
                    if (player == null)
                        continue inner;

                    sendVetoes(player, match, information);
                }
            }

        });
    }

    public void sendVetoes(ProxiedPlayer player, Match match, PoolInformation information) {
        if (!player.isConnected())
            return;

        player.sendMessage(Messages.vetoMessage());
        player.sendMessage(Messages.vetoOptions(information.getMaps()));
        Sounds.playDing(player);
    }

    public void vetoMap(ProxiedPlayer player, String map) throws CommandException {
        Match match = registry.getPlayerMatch(player);
        if (match == null || match.getStatus() != MatchStatus.CREATED)
            throw new CommandException("You can not veto at this time!");

        NervePlugin.async(() -> {
            BoltResponse response = api.veto(match, player.getUniqueId(), new Veto(map));
            if (response.isSuccess())
                player.sendMessage(Messages.vetoed(map));
            else
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + response.getError()));
        });
    }

}
