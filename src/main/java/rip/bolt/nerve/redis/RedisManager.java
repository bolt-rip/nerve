package rip.bolt.nerve.redis;

import java.util.Arrays;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.velocitypowered.api.proxy.ProxyServer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;
import rip.bolt.nerve.event.RedisConnectEvent;
import rip.bolt.nerve.event.RedisMessageEvent;
import rip.bolt.nerve.utils.Executor;

public class RedisManager {

    private ProxyServer server;
    private Executor executor;

    private RedisConfig config;
    private Logger logger;

    private JedisPool pool;

    private static String[] subscriberChannels = { "queue", "match" };

    @Inject
    public RedisManager(ProxyServer server, Executor executor, RedisConfig config, Logger logger) {
        if (!config.enabled())
            return;

        this.server = server;
        this.executor = executor;
        this.config = config;
        this.logger = logger;

        connect();
        startSubscriberThread();
    }

    public void sendRedisMessage(String channel, String message) {
        if (config.enabled()) {
            Jedis jedis = pool.getResource();
            try {
                jedis.publish(channel, message);
            } finally {
                jedis.close();
            }
        } else if (Arrays.asList(subscriberChannels).contains(channel)) { // fire the event since if redis was running, we would receive the event from the subscriber anyway
            server.getEventManager().fire(new RedisMessageEvent(channel, message));
        }
    }

    public boolean connect() {
        try {
            if (pool != null)
                pool.destroy();

            logger.info("Connecting to Redis...");
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            pool = new JedisPool(poolConfig, config.host(), config.port(), 5000, config.password());
        } catch (Throwable e) {
            logger.warn("Failed to connect to Redis! " + e.toString());
            return false;
        }

        return true;
    }

    public void startSubscriberThread() {
        executor.async(() -> {
            while (!Thread.interrupted() && !pool.isClosed()) {
                try (Jedis jedis = pool.getResource()) {
                    logger.info("Connected to Redis!");
                    server.getEventManager().fire(new RedisConnectEvent());

                    jedis.subscribe(new JedisPubSub() {

                        @Override
                        public void onMessage(String channel, String message) {
                            server.getEventManager().fire(new RedisMessageEvent(channel, message));
                        }

                    }, subscriberChannels);
                } catch (JedisConnectionException e) {
                    logger.warn("Redis subscriber failed to connect!");

                    try {
                        Thread.sleep(((long) config.reconnect_sleep()) * 1000);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
    }

}
