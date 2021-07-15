package rip.bolt.nerve.match;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocitypowered.api.event.Subscribe;

import rip.bolt.nerve.api.APIManager;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.event.RedisConnectEvent;
import rip.bolt.nerve.event.RedisMessageEvent;
import rip.bolt.nerve.inject.listener.Listener;
import rip.bolt.nerve.utils.Executor;

public class MatchUpdateListener implements Listener {

    private Executor executor;
    private APIManager api;
    private MatchRegistry registry;
    private ObjectMapper objectMapper;

    @Inject
    public MatchUpdateListener(Executor executor, APIManager api, MatchRegistry registry, ObjectMapper objectMapper) {
        this.executor = executor;
        this.api = api;
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    @Subscribe
    public void onMatchStatusUpdate(RedisMessageEvent event) {
        if (!event.getChannel().equals("match"))
            return;

        try {
            Match match = objectMapper.readValue(event.getMessage(), Match.class);
            if (match.getStatus() == MatchStatus.ENDED || match.getStatus() == MatchStatus.CANCELLED)
                registry.removeMatch(match);
            else
                registry.addMatch(match);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onRedisConnect(RedisConnectEvent event) {
        executor.async(() -> {
            List<Match> runningMatches = api.matches(); // contact the api to sync match states
            if (runningMatches == null)
                return;

            List<String> ids = new ArrayList<String>(registry.getLatestMatches().keySet());

            for (Match match : runningMatches) {
                Match stored = registry.getMatchFromId(match.getId());
                ids.remove(match.getId());

                if (stored == null || match.getStatus() != stored.getStatus())
                    registry.addMatch(match);
            }

            for (String id : ids) // these matches have ended or been cancelled
                registry.removeMatch(registry.getMatchFromId(id));
        });
    }

}
