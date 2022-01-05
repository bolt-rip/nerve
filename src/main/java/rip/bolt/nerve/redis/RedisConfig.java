package rip.bolt.nerve.redis;

import javax.annotation.Nullable;

import rip.bolt.nerve.document.Document;
import rip.bolt.nerve.document.DocumentFieldName;
import rip.bolt.nerve.inject.config.Section;

@Section("redis")
public interface RedisConfig extends Document {

    default boolean enabled() {
        return false;
    }

    String host();

    @Nullable
    String password();

    default int port() {
        return 6379;
    }

    @DocumentFieldName("reconnect-sleep")
    default int reconnect_sleep() {
        return 15;
    }

}
