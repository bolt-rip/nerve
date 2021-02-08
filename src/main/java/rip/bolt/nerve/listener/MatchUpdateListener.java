package rip.bolt.nerve.listener;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.event.RedisMessageEvent;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.MatchStatus;
import rip.bolt.nerve.managers.AutomoveManager;

public class MatchUpdateListener implements Listener {

    private AutomoveManager automoveManager;

    public MatchUpdateListener() {
        this.automoveManager = NervePlugin.getInstance().getAutomoveManager();
    }

    @EventHandler
    public void onMatchStatusUpdate(RedisMessageEvent event) {
        if (!event.getChannel().equals("match"))
            return;

        try {
            Match match = new ObjectMapper().readValue(event.getMessage(), Match.class);
            if (match.getStatus() == MatchStatus.LOADED) automoveManager.addMatch(match);
            else if (match.getStatus() == MatchStatus.ENDED || match.getStatus() == MatchStatus.CANCELLED) automoveManager.removeMatch(match);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
