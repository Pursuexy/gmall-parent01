package com.itcast.gmall.portal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "gmall.pool.main")
public class MainPoolProperties {
	private Integer coreSize;
	private Integer maximumPoolSize;
	private Integer queueSize;
}
