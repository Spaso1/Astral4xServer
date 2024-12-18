package org.astral.astral4xserver.config;

import org.astral.astral4xserver.been.Auth;
import org.astral.astral4xserver.been.User;
import org.astral.astral4xserver.cache.TimeLimitedCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public TimeLimitedCache<String, User> timeLimitedCache() {
        return new TimeLimitedCache<>(5 * 60 * 1000); // 5分钟过期时间
    }
    @Bean
    public TimeLimitedCache<String, Auth> time2LimitedCache() {
        return new TimeLimitedCache<>(24 * 60 * 60 * 1000); // 1天过期时间
    }
    @Bean
    public TimeLimitedCache<String, String> time3LimitedCache() {
        return new TimeLimitedCache<>(5 * 60 * 1000); // 5分钟过期时间
    }
}
