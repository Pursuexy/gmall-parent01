package com.itcast.gmall.pms.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import io.shardingjdbc.core.api.MasterSlaveDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@Configuration
public class PmsDataSourceConfig {
	/**
	 * 数据源插件
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	@Bean
	public DataSource dataSource() throws IOException, SQLException {
		File file = ResourceUtils.getFile("classpath:sharding-jdbc");
		DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(file);
		return dataSource;
	}

	/**
	 * 分页插件
	 * @return
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}
}
