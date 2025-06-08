package com.julie.store.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(30); // Support all tasks
        executor.setMaxPoolSize(60);  // Allow scaling
        executor.setQueueCapacity(400); // Queue tasks if needed
        executor.setThreadNamePrefix("Async-Thread-");
        executor.setWaitForTasksToCompleteOnShutdown(true); // Wait for tasks on shutdown
        executor.setAwaitTerminationSeconds(60); // Wait up to 60 seconds
        executor.initialize();
        return executor;
    }
}