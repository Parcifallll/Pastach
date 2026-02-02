package com.example.Pastach.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // min threads
        executor.setMaxPoolSize(25);
        executor.setQueueCapacity(50); // waited tasks in blocking-queue
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();

        // for Principal, because securityContext is stored in ThreadLocal
        return new DelegatingSecurityContextExecutor(executor);
    }
}