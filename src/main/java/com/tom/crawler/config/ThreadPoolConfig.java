package com.tom.crawler.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;


@Configuration
public class ThreadPoolConfig {

    private static final int CORE_POOL_SIZE = 3;

    private static final int MAX_POOL_SIZE = 8;

    @Bean
    public ExecutorService executorService() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("crawler-pool-%d").build();
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5000);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                0L, TimeUnit.MILLISECONDS, workQueue, threadFactory);
    }

}
