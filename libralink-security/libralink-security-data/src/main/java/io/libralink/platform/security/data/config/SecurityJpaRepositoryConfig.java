package io.libralink.platform.security.data.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("io.libralink.platform.security.data.entity")
@EnableJpaRepositories(basePackages = { "io.libralink.platform.security.data.repository" })
public class SecurityJpaRepositoryConfig {
}
