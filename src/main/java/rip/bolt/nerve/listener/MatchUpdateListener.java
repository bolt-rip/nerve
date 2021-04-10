package rip.bolt.nerve.listener;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.event.RedisMessageEvent;
import rip.bolt.nerve.managers.MatchRegistry;

public class MatchUpdateListener implements Listener {

    private MatchRegistry registry;
    private ObjectMapper objectMapper;

    public MatchUpdateListener(MatchRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    @EventHandler
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

}
