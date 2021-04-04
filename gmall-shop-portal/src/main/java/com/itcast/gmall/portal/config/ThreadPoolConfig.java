package com.itcast.gmall.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 配置当前系统的线程池
 */
@Configuration
public class ThreadPoolConfig {

	@Bean("mainThreadPoolExecutor")
	public ThreadPoolExecutor mainThreadPoolExecutor(MainPoolProperties mainPoolProperties) {
		LinkedBlockingDeque<Runnable> deque = new LinkedBlockingDeque<>(mainPoolProperties.getQueueSize());
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(mainPoolProperties.getCoreSize(), mainPoolProperties.getMaximumPoolSize(), 10, TimeUnit.MINUTES, deque);
		return threadPoolExecutor;
	}

	@Bean("otherThreadPoolExecutor")
	public ThreadPoolExecutor otherThreadPoolExecutor(OtherPoolProperties otherPoolProperties) {
		LinkedBlockingDeque<Runnable> deque = new LinkedBlockingDeque<>(otherPoolProperties.getQueueSize());
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(otherPoolProperties.getCoreSize(), otherPoolProperties.getMaximumPoolSize(), 10, TimeUnit.MINUTES, deque);
		return threadPoolExecutor;
	}
}
