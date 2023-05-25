package com.haizhi.empower.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步线程池
 *
 * @author ：lvchengfei
 * @date ：2023/2/16 17:10
 */
@Configuration
@Slf4j
public class AsyncThreadPoolConfig {

    /** 线程池中的核心线程数量 */
    private int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;//获得虚拟机处理器的数量+1
    /** 线程池中允许线程的空闲时间 s */
    private int keepAliveTime = 10;
    /** 线程池中的队列最大数量 */
    private int queueCapacity = 300;

    /** 线程的名称前缀 */
    private String threadNamePrefix = "async-thread-";

    @Bean(name = "ThreadPoolExecutor")
    public ThreadPoolTaskExecutor systemCheckPoolExecutorService() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        taskExecutor.setCorePoolSize(corePoolSize);
        // 线程池维护线程的最大数量，只有在缓冲队列满了以后才会申请超过核心线程数的线程
        taskExecutor.setMaxPoolSize(corePoolSize * 2);
        //缓存队列
        taskExecutor.setQueueCapacity(queueCapacity);
        //允许的空闲时间，当超过了核心线程数之外的线程在空闲时间到达之后会被销毁
        taskExecutor.setKeepAliveSeconds(keepAliveTime);
        //异步方法内部线程名称
        taskExecutor.setThreadNamePrefix(threadNamePrefix);
        //拒绝策略
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        taskExecutor.initialize();

        return taskExecutor;
    }
}
