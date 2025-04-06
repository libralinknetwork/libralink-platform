package io.libralink.platform.suite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:datasource.properties")
@EnableTransactionManagement
public class DatasourceConfiguration {
}
