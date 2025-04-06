package io.libralink.platform.wallet.data.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("io.libralink.platform.wallet.data.entity")
@EnableJpaRepositories(basePackages = { "io.libralink.platform.wallet.data.repository" })
public class WalletJpaRepositoryConfig {
}
