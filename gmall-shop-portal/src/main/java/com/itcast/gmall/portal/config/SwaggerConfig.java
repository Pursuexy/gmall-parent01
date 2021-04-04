package com.itcast.gmall.portal.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean("检索模块")
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("检索模块")
				.select()
				.apis(RequestHandlerSelectors.withMethodAnnotation(Api.class))
				.paths(PathSelectors.regex("/search.*"))
				.build()
				.apiInfo(apiInfo())
				.enable(true);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("谷粒商城-检索模块平台接口文档")
				.description("提供检索模块的文档")
				.version("1.0")
				.build();
	}
}
