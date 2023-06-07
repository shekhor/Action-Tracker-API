package com.tigerit.soa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/*
Fahim created at 4/8/2020
*/
@Configuration
@EnableAsync
@ComponentScan("com.tigerit")
public class AsyncConfig extends AsyncConfigurerSupport {

    @Value("${thread.identification.corePoolSize}")
    private int corePoolSize;
    @Value("${thread.identification.maxPoolSize}")
    private int maxPoolSize;
    @Value("${thread.identification.queueCapacity}")
    private int queueCapacity;
    @Value("${thread.identification.namePrefix}")
    private String namePrefix;

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(namePrefix);
        executor.initialize();
        return executor;
    }
}
