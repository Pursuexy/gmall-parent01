package com.itcast.gmall.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 监控线程池的健康状况
 */
@RestController
public class ThreadPoolController {
	@Qualifier("mainThreadPoolExecutor")
	@Autowired
	private ThreadPoolExecutor mainThreadPoolExecutor;

	@Qualifier("otherThreadPoolExecutor")
	@Autowired
	private ThreadPoolExecutor otherThreadPoolExecutor;

	@GetMapping("/thread/status/0")
	public Map<String,Object> mainThreadPoolStatus() {
		Map<String, Object> map = new HashMap<>();
		map.put("activeCount", mainThreadPoolExecutor.getActiveCount());
		map.put("completedTaskCount", mainThreadPoolExecutor.getCompletedTaskCount());
		map.put("corePoolSize", mainThreadPoolExecutor.getCorePoolSize());
		map.put("maximumPoolSize", mainThreadPoolExecutor.getMaximumPoolSize());
		map.put("poolSize", mainThreadPoolExecutor.getPoolSize());
		map.put("queue", mainThreadPoolExecutor.getQueue());
		return map;
	}

	@GetMapping("/thread/status/1")
	public Map<String,Object> otherThreadPoolStatus() {
		Map<String, Object> map = new HashMap<>();
		map.put("activeCount", otherThreadPoolExecutor.getActiveCount());
		map.put("completedTaskCount", otherThreadPoolExecutor.getCompletedTaskCount());
		map.put("corePoolSize", otherThreadPoolExecutor.getCorePoolSize());
		map.put("maximumPoolSize", otherThreadPoolExecutor.getMaximumPoolSize());
		map.put("poolSize", otherThreadPoolExecutor.getPoolSize());
		map.put("queue", otherThreadPoolExecutor.getQueue());
		return map;
	}
}
