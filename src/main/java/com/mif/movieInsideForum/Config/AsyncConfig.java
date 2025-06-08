package com.mif.movieInsideForum.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Số lượng luồng chính
        executor.setMaxPoolSize(2); // Số lượng luồng tối đa
        executor.setQueueCapacity(500); // Sức chứa hàng đợi
        executor.setThreadNamePrefix("ActorRankings-"); // Tiền tố tên luồng
        executor.initialize();
        return executor;
    }
}