package rip.bolt.nerve.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import rip.bolt.nerve.inject.config.ConfigBinder;

public class APIModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ObjectMapper.class).toInstance(new ObjectMapper().registerModule(new DateModule()));
        bind(APIManager.class).in(Singleton.class);

        new ConfigBinder(binder()).register(APIConfig.class);
    }

}
