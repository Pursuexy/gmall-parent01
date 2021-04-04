package com.itcast.gmall.portal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("gmall.pool.other")
public class OtherPoolProperties {
	private Integer coreSize;
	private Integer maximumPoolSize;
	private Integer queueSize;
}
