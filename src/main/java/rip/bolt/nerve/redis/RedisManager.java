package rip.bolt.nerve.redis;

import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.config.AppData;
import rip.bolt.nerve.event.RedisMessageEvent;

public class RedisManager {

    private JedisPool pool;

    private static List<String> publisherChannels = Arrays.asList();
    private static String[] subscriberChannels = { "queue" };

    public RedisManager() {
        if (!AppData.Redis.isEnabled())
            return;

        connect();
        startSubscriberThread();
    }

    public void sendRedisMessage(String channel, String message) {
        if (!publisherChannels.contains(channel))
            throw new IllegalArgumentException("Channel " + channel + " is not registered for publishing!");

        if (AppData.Redis.isEnabled()) {
            Jedis jedis = pool.getResource();
            try {
                jedis.publish(channel, message);
            } finally {
                jedis.close();
            }
        } else if (Arrays.asList(subscriberChannels).contains(channel)) {// fire the event since if redis was running, we would receive the event from the subscriber anyway
            ProxyServer.getInstance().getPluginManager().callEvent(new RedisMessageEvent(channel, message));
        }
    }

    public boolean connect() {
        try {
            if (pool != null)
                pool.destroy();

            System.out.println("[Nerve] Connecting to Redis...");
            JedisPoolConfig config = new JedisPoolConfig();
            pool = new JedisPool(config, AppData.Redis.getHost(), AppData.Redis.getPort(), 5000, AppData.Redis.getPassword());
        } catch (Throwable e) {
            System.out.println("[Nerve] Failed to connect!");
            return false;
        }

        return true;
    }

    public void startSubscriberThread() {
        ProxyServer.getInstance().getScheduler().runAsync(NervePlugin.getInstance(), new Runnable() {

            public void run() {
                while (!Thread.interrupted() && !pool.isClosed()) {
                    try (Jedis jedis = pool.getResource()) {
                        System.out.println("[Nerve] Connected to Redis!");
                        jedis.subscribe(new JedisPubSub() {

                            @Override
                            public void onMessage(String channel, String message) {
                                ProxyServer.getInstance().getPluginManager().callEvent(new RedisMessageEvent(channel, message));
                            }

                        }, subscriberChannels);
                    } catch (JedisConnectionException e) {
                        System.out.println("[Nerve] Redis subscriber failed to connect!");

                        try {
                            Thread.sleep(((long) AppData.Redis.getReconnectSleep()) * 1000);
                        } catch (InterruptedException e1) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

        });
    }

}
