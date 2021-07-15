package rip.bolt.nerve.redis;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import rip.bolt.nerve.inject.config.ConfigBinder;

public class RedisModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RedisManager.class).in(Singleton.class);
        new ConfigBinder(binder()).register(RedisConfig.class);
    }

}
