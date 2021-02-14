package rip.bolt.nerve.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;

public class MatchRegistry {

    private HashMap<String, Match> matches = new HashMap<String, Match>();
    private List<MatchStatusListener> listeners = new ArrayList<MatchStatusListener>();

    public void registerListener(MatchStatusListener listener) {
        listeners.add(listener);
    }

    public void onMatchStatusUpdate(Match match) {
        for (MatchStatusListener listener : listeners)
            listener.matchStatusUpdate(match);
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
                    if (participant.getUUID().equals(uuid))
                        return match;
                }
            }
        }

        return null;
    }

    public Match getMatchFromServerName(String serverName) {
        if (matches == null)
            return null;

        for (Match match : matches.values()) {
            if (match.getServer().equals(serverName))
                return match;
        }

        return null;
    }

    public HashMap<String, Match> getLatestMatches() {
        return matches;
    }

    public Match addMatch(Match match) {
        matches.put(match.getMatchId(), match);
        onMatchStatusUpdate(match);

        return match;
    }

    public Match removeMatch(Match match) {
        return matches.remove(match.getMatchId());
    }

}
