package rip.bolt.nerve.managers;

import java.util.Arrays;

import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.config.AppData;
import rip.bolt.nerve.event.RedisMessageEvent;

public class RedisManager {

    private Jedis subscriber, publisher;

    private static String[] channels = { "queue" };

    public RedisManager() {
        if (!AppData.Redis.isEnabled())
            return;

        subscriber = new Jedis(AppData.Redis.getHost(), AppData.Redis.getPort());
        publisher = new Jedis(AppData.Redis.getHost(), AppData.Redis.getPort());

        ProxyServer.getInstance().getScheduler().runAsync(NervePlugin.getInstance(), new Runnable() {

            public void run() {
                subscriber.subscribe(new JedisPubSub() {

                    @Override
                    public void onMessage(String channel, String message) {
                        ProxyServer.getInstance().getPluginManager().callEvent(new RedisMessageEvent(channel, message));
                    }

                }, channels);
            }

        });
    }

    public void sendRedisMessage(String channel, String message) {
        if (!Arrays.asList(channels).contains(channel))
            throw new IllegalArgumentException("Channel " + channel + " is not registered!");

        if (AppData.Redis.isEnabled())
            publisher.publish(channel, message);
        else // fire the event since if redis was running, we would receieve the event from the subscriber anyway
            ProxyServer.getInstance().getPluginManager().callEvent(new RedisMessageEvent(channel, message));
    }

}