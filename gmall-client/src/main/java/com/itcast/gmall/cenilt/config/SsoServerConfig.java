package com.itcast.gmall.cenilt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sso.server")
public class SsoServerConfig {

	private String url;
	private String loginPath;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLoginPath() {
		return loginPath;
	}

	public void setLoginPath(String loginPath) {
		this.loginPath = loginPath;
	}

}
