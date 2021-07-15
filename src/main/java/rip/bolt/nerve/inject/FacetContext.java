package rip.bolt.nerve.inject;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;

import com.velocitypowered.api.event.EventManager;

import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.inject.commands.CommandRegistry;
import rip.bolt.nerve.inject.commands.Commands;
import rip.bolt.nerve.inject.listener.Listener;
import rip.bolt.nerve.match.MatchRegistry;
import rip.bolt.nerve.match.MatchStatusListener;

public class FacetContext {

    private EventManager events;
    private CommandRegistry commandRegistry;
    private MatchRegistry matches;

    private NervePlugin plugin;
    private Logger logger;

    private Provider<Set<Facet>> facets;

    @Inject
    public FacetContext(EventManager events, CommandRegistry commandRegistry, MatchRegistry matches, NervePlugin plugin, Logger logger, Provider<Set<Facet>> facets) {
        this.events = events;
        this.commandRegistry = commandRegistry;
        this.matches = matches;

        this.plugin = plugin;
        this.logger = logger;

        this.facets = facets;

        init();
    }

    public void init() {
        int count = 0;
        for (Facet facet : facets.get()) {
            if (facet instanceof Listener) // maybe check if it has any @Subscribe methods instead?
                events.register(plugin, facet);

            if (facet instanceof Commands)
                commandRegistry.register(facet.getClass());

            if (facet instanceof MatchStatusListener)
                matches.registerListener((MatchStatusListener) facet);
            count++;
        }

        logger.info(count + " facets enabled.");
    }

}
