package rip.bolt.nerve.match;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import rip.bolt.nerve.inject.FacetBinder;

public class MatchModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MatchRegistry.class).in(Singleton.class);

        FacetBinder listener = new FacetBinder(binder());
        listener.register(JoinListener.class);
        listener.register(MatchUpdateListener.class);
    }

}
