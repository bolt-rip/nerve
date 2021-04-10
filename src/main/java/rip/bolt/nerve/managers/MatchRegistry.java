package rip.bolt.nerve.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.APIManager;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.event.RedisConnectEvent;

public class MatchRegistry implements Listener {

    private APIManager api;
    private HashMap<String, Match> matches = new HashMap<String, Match>();
    private List<MatchStatusListener> listeners = new ArrayList<MatchStatusListener>();

    private static final long MISSED_MESSAGE_DELAY = 30 * 60 * 1000;

    public MatchRegistry(APIManager api) {
        this.api = api;

        ProxyServer.getInstance().getScheduler().schedule(NervePlugin.getInstance(), () -> {
            // remove any match that hasn't had an update in 30 minutes (in case the redis crashed or a message was dropped)

            long time = System.currentTimeMillis();
            for (Match match : matches.values()) {
                long timeElapsed = time - match.getLastUpdateTime();
                if (timeElapsed > MISSED_MESSAGE_DELAY) {
                    System.out.println("[Nerve] Match " + match.getId() + " on server " + match.getServer() + " hasn't had an update in " + timeElapsed + "ms. Automatically removing...");
                    removeMatch(match);
                }
            }

        }, 5, 5, TimeUnit.MINUTES);
        ProxyServer.getInstance().getPluginManager().registerListener(NervePlugin.getInstance(), this);
    }

    @EventHandler
    public void onRedisConnect(RedisConnectEvent event) {
        NervePlugin.async(() -> {
            List<Match> runningMatches = api.matches(); // contact the api to sync match states
            if (runningMatches == null)
                return;

            List<String> ids = new ArrayList<String>(matches.keySet());

            for (Match match : runningMatches) {
                Match stored = matches.get(match.getId());
                ids.remove(match.getId());

                if (stored == null || match.getStatus() != stored.getStatus())
                    addMatch(match);
            }

            for (String id : ids) // these matches have ended or been cancelled
                removeMatch(matches.get(id));
        });
    }

    public void registerListener(MatchStatusListener listener) {
        listeners.add(listener);
    }

    public void onMatchStatusUpdate(Match match) {
        NervePlugin.async(() -> {
            for (MatchStatusListener listener : listeners)
                listener.matchStatusUpdate(match);
        });
    }

    public void onPlayerJoin(ProxiedPlayer player, Match match) {
        for (MatchStatusListener listener : listeners)
            listener.playerJoin(player, match);
    }

    public Match getPlayerMatch(ProxiedPlayer player) {
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
