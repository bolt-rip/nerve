package rip.bolt.nerve.event;

public class RedisMessageEvent {

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
