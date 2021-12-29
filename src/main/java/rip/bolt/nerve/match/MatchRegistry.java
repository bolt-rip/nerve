package rip.bolt.nerve.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.utils.Executor;

public class MatchRegistry {

    private Executor executor;

    private HashMap<String, Match> matches = new HashMap<String, Match>();
    private List<MatchStatusListener> listeners = new ArrayList<MatchStatusListener>();

    private static final long MISSED_MESSAGE_DELAY = 30 * 60 * 1000;

    @Inject
    public MatchRegistry(ProxyServer server, Executor executor, NervePlugin plugin, Logger logger) {
        this.executor = executor;

        server.getScheduler().buildTask(plugin, () -> {
            // remove any match that hasn't had an update in 30 minutes (in case the redis crashed or a message was dropped)
            long time = System.currentTimeMillis();
            for (Match match : matches.values()) {
                long timeElapsed = time - match.getLastUpdateTime();
                if (timeElapsed > MISSED_MESSAGE_DELAY) {
                    logger.warn("Match " + match.getId() + " on server " + match.getServer() + " hasn't had an update in " + timeElapsed + "ms. Automatically removing...");
                    removeMatch(match);
                }
            }
        }).delay(5, TimeUnit.MINUTES).repeat(5, TimeUnit.MINUTES).schedule();
    }

    public void registerListener(MatchStatusListener listener) {
        listeners.add(listener);
    }

    public void onMatchStatusUpdate(Match match) {
        executor.async(() -> {
            for (MatchStatusListener listener : listeners)
                listener.matchStatusUpdate(match);
        });
    }

    public void onPlayerJoin(Player player, Match match) {
        for (MatchStatusListener listener : listeners)
            listener.playerJoin(player, match);
    }

    public Match getPlayerMatch(Player player) {
        return getPlayerMatch(player.getUniqueId());
    }

    public Match getPlayerMatch(UUID uuid) {
        if (matches == null)
            return null;

        for (Match match : matches.values()) {
            for (Team team : match.getTeams()) {
                for (User participant : team.getPlayers()) {
                    if (participant.getUniqueId().equals(uuid))
                        return match;
                }
            }
        }

        return null;
    }

    public Match getMatchFromServerName(String serverName) {
        for (Match match : matches.values()) {
            if (match.getServer().equals(serverName))
                return match;
        }

        return null;
    }

    public Match getMatchFromId(String id) {
        return matches.get(id);
    }

    public HashMap<String, Match> getLatestMatches() {
        return matches;
    }

    public Match addMatch(Match match) {
        matches.put(match.getId(), match);
        onMatchStatusUpdate(match);

        return match;
    }

    public Match removeMatch(Match match) {
        return matches.remove(match.getId());
    }

}
