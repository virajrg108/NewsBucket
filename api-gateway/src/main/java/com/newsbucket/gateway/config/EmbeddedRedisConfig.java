package com.newsbucket.gateway.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
@ConditionalOnProperty(name = "spring.redis.embedded", havingValue = "true")
public class EmbeddedRedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedRedisConfig.class);

    private RedisServer redisServer;

    @Value("${spring.redis.embedded.port:6379}")
    private int redisPort;

    @PostConstruct
    public void startRedis() throws IOException {
        try {
            logger.info("Starting embedded Redis server on port {}...", redisPort);
            redisServer = new RedisServer(redisPort);
            redisServer.start();
            logger.info("Embedded Redis server started successfully");
        } catch (Exception e) {
            logger.error("Failed to start embedded Redis server", e);
            // If Redis is already running, we can continue
            if (e.getMessage() != null && e.getMessage().contains("Address already in use")) {
                logger.warn("Redis server is already running on port {}", redisPort);
            } else {
                throw e;
            }
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            logger.info("Stopping embedded Redis server...");
            redisServer.stop();
            logger.info("Embedded Redis server stopped");
        }
    }
}