package io.libralink.platform.agent.data.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("io.libralink.platform.agent.data.entity")
@EnableJpaRepositories(basePackages = { "io.libralink.platform.agent.data.repository" })
public class AgentJpaRepositoryConfig {
}
