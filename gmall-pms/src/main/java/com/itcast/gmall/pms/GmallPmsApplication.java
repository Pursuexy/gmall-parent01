package com.itcast.gmall.pms;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableDubbo
@MapperScan(basePackages = "com.itcast.gmall.pms.mapper")
@SpringBootApplication
public class GmallPmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallPmsApplication.class, args);
	}

}
