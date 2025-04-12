package io.libralink.platform.suite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:processor.properties")
public class ProcessorConfiguration {
}
