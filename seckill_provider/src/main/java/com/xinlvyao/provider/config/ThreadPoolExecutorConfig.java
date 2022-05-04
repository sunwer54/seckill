package com.xinlvyao.provider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolExecutorConfig {
    @Bean
    public Executor asyncExecutor(){
        //创建线程池
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置线程池核心线程数量
        executor.setCorePoolSize(10);
        //设置线程池最大线程数
        executor.setMaxPoolSize(100);
        //设置线程的最大空闲时间,1800s
        executor.setKeepAliveSeconds(3000);
        //设置任务队列的长度(即队列中可以暂存的任务的最大数量)
        executor.setQueueCapacity(200);
        //设置线程池中线程名称的前缀
        executor.setThreadNamePrefix("async-seckill-");
        /*设置线程池的饱和策略:即当线程池中的核心线程都不空闲且达到任务处理
          最大线程数，任务队列存储的任务也达到了最大数量，此时线程池处于饱和状态。
          当达到线程池饱和状态时，超过饱和状态下的任务的处理策略：
          CallerRunsPolicy():不在新线程中执行任务,而是在调用者所在的线程中执行*/
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }
}
