package rip.bolt.nerve.match.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import rip.bolt.nerve.api.definitions.PGMMap;
import rip.bolt.nerve.api.definitions.Pool;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.match.MatchStatusListener;
import rip.bolt.nerve.utils.Executor;
import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Sounds;

public class VetoManager implements MatchStatusListener {

    private Executor executor;
    private ProxyServer server;

    private NervePlugin plugin;
    private APIManager api;

    private Sounds sounds;

    private Map<String, Pool> pools;
    private Set<UUID> vetoed;

    private static final TextComponent vetoMessage = Messages.vetoMessage();

    @Inject
    public VetoManager(Executor executor, ProxyServer server, NervePlugin plugin, APIManager api, Sounds sounds) {
        this.executor = executor;
        this.server = server;

        this.plugin = plugin;
        this.api = api;

        this.sounds = sounds;

        this.pools = new HashMap<String, Pool>();
        this.vetoed = new HashSet<UUID>();
    }

    @Override
    public void playerJoin(Player player, Match match) {
        if (match.getStatus() != MatchStatus.CREATED)
            return;

        Pool pool = match.getPool();
        if (pool == null)
            pool = pools.get(match.getId());

        if (pool == null)
            return; // they will be sent the vetoes in a moment

        TextComponent vetoOptions = Messages.vetoOptions(pool.getMaps());
        server.getScheduler().buildTask(plugin, () -> {
            if (match.getStatus() == MatchStatus.CREATED && match.getMap() == null)
                sendVetoes(player, match, vetoOptions);
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
            int seriesId = match.getSeriesId();

            Pool pool = match.getPool();
            if (pool == null)
                pools.put(match.getId(), pool = api.getPool(seriesId, queueSize));

            TextComponent vetoOptions = Messages.vetoOptions(pool.getMaps());

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
        sounds.playDing(player);
    }

    public void vetoMap(Player player, Match match, String query) {
        PGMMap found = null;
        Pool pool = match.getPool();

        if (pool == null)
            pool = pools.get(match.getId());

        if (pool == null) {
            player.sendMessage(Component.text("Please wait a few seconds before running this command again.").color(NamedTextColor.RED));
            return;
        }

        for (PGMMap map : pool.getMaps()) {
            if (map.getName().toLowerCase().startsWith(query.toLowerCase())) {
                found = map;
                break;
            }
        }

        if (found == null) {
            player.sendMessage(Messages.mapNotFound(query));
            return;
        }

        BoltResponse response = api.veto(match, player.getUniqueId(), found);
        if (response.isSuccess()) {
            player.sendMessage(Messages.vetoed(found, null, vetoed.contains(player.getUniqueId())));
            for (User user : match.getPlayerTeam(player).getPlayers()) {
                if (user.getUniqueId().equals(player.getUniqueId()))
                    continue;

                Optional<Player> viewer = server.getPlayer(user.getUniqueId());
                if (!viewer.isPresent())
                    continue;

                viewer.get().sendMessage(Messages.vetoed(found, player.getUsername(), vetoed.contains(player.getUniqueId())));
            }

            vetoed.add(player.getUniqueId());
        } else {
            player.sendMessage(Component.text(response.getError()).color(NamedTextColor.RED));
        }
    }

}
