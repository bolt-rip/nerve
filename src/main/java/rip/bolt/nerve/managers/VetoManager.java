package rip.bolt.nerve.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.sk89q.minecraft.util.commands.ChatColor;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.APIManager;
import rip.bolt.nerve.api.definitions.BoltResponse;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.api.definitions.PoolInformation;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.api.definitions.Veto;
import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Sounds;

public class VetoManager implements MatchStatusListener {

    private APIManager api;
    private Map<String, PoolInformation> pools;
    private Set<UUID> vetoed;

    private static final BaseComponent[] vetoMessage = Messages.vetoMessage();

    public VetoManager(APIManager api) {
        this.api = api;
        this.pools = new HashMap<String, PoolInformation>();
        this.vetoed = new HashSet<UUID>();
    }

    @Override
    public void playerJoin(ProxiedPlayer player, Match match) {
        if (match.getStatus() != MatchStatus.CREATED)
            return;

        PoolInformation information = pools.get(match.getId());
        if (information == null)
            return; // they will be sent the vetoes in a moment

        ProxyServer.getInstance().getScheduler().schedule(NervePlugin.getInstance(), () -> {
            if (match.getStatus() == MatchStatus.CREATED && match.getMap() == null)
                sendVetoes(player, match, Messages.vetoOptions(information.getMaps()));
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void matchStatusUpdate(Match match) {
        if (match.getStatus() != MatchStatus.CREATED)
            return;

        if (match.getMap() != null) {
            pools.remove(match.getId());
            BaseComponent[] mapDecided = Messages.mapDecided(match.getMap());

            for (Team team : match.getTeams()) {
                inner: for (User participant : team.getPlayers()) {
                    vetoed.remove(participant.getUniqueId());
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(participant.getUniqueId());
                    if (player == null)
                        continue inner;

                    player.sendMessage(mapDecided);
                }
            }

            return;
        }

        NervePlugin.async(() -> {
            int queueSize = match.getQueueSize();
            PoolInformation information = api.getPoolInformation(queueSize);
            pools.put(match.getId(), information);

            BaseComponent[] vetoOptions = Messages.vetoOptions(information.getMaps());

            for (Team team : match.getTeams()) {
                inner: for (User participant : team.getPlayers()) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(participant.getUniqueId());
                    if (player == null)
                        continue inner;

                    sendVetoes(player, match, vetoOptions);
                }
            }

        });
    }

    public void sendVetoes(ProxiedPlayer player, Match match, BaseComponent[] vetoOptions) {
        if (!player.isConnected())
            return;

        player.sendMessage(vetoMessage);
        player.sendMessage(vetoOptions);
        Sounds.playDing(player);
    }

    public void vetoMap(ProxiedPlayer player, Match match, String query) {
        String found = null;
        for (String map : pools.get(match.getId()).getMaps()) {
            if (map.toLowerCase().startsWith(query.toLowerCase())) {
                found = map;
                break;
            }
        }

        if (found == null) {
            player.sendMessage(Messages.mapNotFound(query));
            return;
        }

        BoltResponse response = api.veto(match, player.getUniqueId(), new Veto(found));
        if (response.isSuccess()) {
            player.sendMessage(Messages.vetoed(found, vetoed.contains(player.getUniqueId())));
            vetoed.add(player.getUniqueId());
        } else {
            player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + response.getError()));
        }
    }

}
