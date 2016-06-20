package com.zhangyan.util;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;


@Configuration
public class DataSourceConfig {
	
	@Primary
	@Bean(name = "jobDs")
	@ConfigurationProperties(prefix = "dataSource_dbfreeradius")
	public DataSource freeradiusDs() {
		return DataSourceBuilder.create().build();
	}
	
	
	@Bean(name = "jobJdbc")
	public JdbcTemplate freeradiusJdbc(
			@Qualifier(value = "jobDs") DataSource jobDs) {
		return new JdbcTemplate(jobDs);
	}
	

}
