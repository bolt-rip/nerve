package rip.bolt.nerve.match.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.APIManager;
import rip.bolt.nerve.api.definitions.BoltResponse;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.api.definitions.PoolInformation;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.api.definitions.Veto;
import rip.bolt.nerve.match.MatchStatusListener;
import rip.bolt.nerve.utils.Executor;
import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Sounds;

public class VetoManager implements MatchStatusListener {

    private Executor executor;
    private ProxyServer server;
    private NervePlugin plugin;

    private APIManager api;

    private Map<String, PoolInformation> pools;
    private Set<UUID> vetoed;

    private static final TextComponent vetoMessage = Messages.vetoMessage();

    @Inject
    public VetoManager(Executor executor, ProxyServer server, NervePlugin plugin, APIManager api) {
        this.executor = executor;
        this.server = server;
        this.plugin = plugin;

        this.api = api;

        this.pools = new HashMap<String, PoolInformation>();
        this.vetoed = new HashSet<UUID>();
    }

    @Override
    public void playerJoin(Player player, Match match) {
        if (match.getStatus() != MatchStatus.CREATED)
            return;

        PoolInformation information = pools.get(match.getId());
        if (information == null)
            return; // they will be sent the vetoes in a moment

        server.getScheduler().buildTask(plugin, () -> {
            if (match.getStatus() == MatchStatus.CREATED && match.getMap() == null)
                sendVetoes(player, match, Messages.vetoOptions(information.getMaps()));
        }).delay(1, TimeUnit.SECONDS).schedule();
    }

    @Override
    public void matchStatusUpdate(Match match) {
        if (match.getStatus() != MatchStatus.CREATED)
            return;

        if (match.getMap() != null) {
            pools.remove(match.getId());
            TextComponent mapDecided = Messages.mapDecided(match.getMap());

            for (Team team : match.getTeams()) {
                inner: for (User participant : team.getPlayers()) {
                    vetoed.remove(participant.getUniqueId());
                    Optional<Player> player = server.getPlayer(participant.getUniqueId());
                    if (!player.isPresent())
                        continue inner;

                    player.get().sendMessage(mapDecided);
                }
            }

            return;
        }

        executor.async(() -> {
            int queueSize = match.getQueueSize();
            PoolInformation information = api.getPoolInformation(queueSize);
            pools.put(match.getId(), information);

            TextComponent vetoOptions = Messages.vetoOptions(information.getMaps());

            for (Team team : match.getTeams()) {
                inner: for (User participant : team.getPlayers()) {
                    Optional<Player> player = server.getPlayer(participant.getUniqueId());
                    if (!player.isPresent())
                        continue inner;

                    sendVetoes(player.get(), match, vetoOptions);
                }
            }

        });
    }

    public void sendVetoes(Player player, Match match, TextComponent vetoOptions) {
        if (!player.isActive())
            return;

        player.sendMessage(vetoMessage);
        player.sendMessage(vetoOptions);
        Sounds.playDing(player);
    }

    public void vetoMap(Player player, Match match, String query) {
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
            player.sendMessage(Component.text(response.getError()).color(NamedTextColor.RED));
        }
    }

}
