package com.itcast.gmall.admin.config;

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

	@Bean("后台用户模块")
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("后台用户模块")
				.select()
				.apis(RequestHandlerSelectors.withMethodAnnotation(Api.class))
				.paths(PathSelectors.regex("/admin.*"))
				.build()
				.apiInfo(apiInfo())
				.enable(true);
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("谷粒商城-后台管理平台接口文档")
				.description("提供后台管理平台的文档")
				.version("1.0")
				.build();
	}

}
