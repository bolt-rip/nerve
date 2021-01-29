package rip.bolt.nerve.event;

import net.md_5.bungee.api.plugin.Event;

public class RedisMessageEvent extends Event {

    private String channel, message;

    public RedisMessageEvent(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

}
