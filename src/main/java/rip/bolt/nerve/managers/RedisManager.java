package rip.bolt.nerve.managers;

import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.config.AppData;
import rip.bolt.nerve.event.RedisMessageEvent;

public class RedisManager {

    private Jedis subscriber, publisher;

    private static List<String> publisherChannels = Arrays.asList("requeue");
    private static String[] subscriberChannels = { "queue", "requeue-response" };

    public RedisManager() {
        if (!AppData.Redis.isEnabled())
            return;

        System.out.println("[Nerve] Connecting to Redis...");
        subscriber = new Jedis(AppData.Redis.getHost(), AppData.Redis.getPort());
        publisher = new Jedis(AppData.Redis.getHost(), AppData.Redis.getPort());

        ProxyServer.getInstance().getScheduler().runAsync(NervePlugin.getInstance(), new Runnable() {

            public void run() {
                subscriber.subscribe(new JedisPubSub() {

                    @Override
                    public void onMessage(String channel, String message) {
                        ProxyServer.getInstance().getPluginManager().callEvent(new RedisMessageEvent(channel, message));
                    }

                }, subscriberChannels);
            }

        });
    }

    public void sendRedisMessage(String channel, String message) {
        if (!publisherChannels.contains(channel))
            throw new IllegalArgumentException("Channel " + channel + " is not registered for publishing!");

        if (AppData.Redis.isEnabled())
            publisher.publish(channel, message);
        else if (Arrays.asList(subscriberChannels).contains(channel)) // fire the event since if redis was running, we would receieve the event from the subscriber anyway
            ProxyServer.getInstance().getPluginManager().callEvent(new RedisMessageEvent(channel, message));
    }

}