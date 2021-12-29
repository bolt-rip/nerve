package rip.bolt.nerve.privateserver;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import rip.bolt.nerve.inject.FacetBinder;
import rip.bolt.nerve.inject.config.ConfigBinder;

public class PrivateServerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PrivateServerRequester.class).in(Singleton.class);
        bind(ServerAddedListener.class).in(Singleton.class);

        new ConfigBinder(binder()).register(PrivateServerConfig.class);
        new FacetBinder(binder()).register(PrivateCommand.class);
    }

}
