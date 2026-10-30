package com.juniverse.hexagonal.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Activa los repositorios JPA del paquete de adaptadores de salida y la gestión de transacciones. */
@Configuration
@EnableJpaRepositories(basePackages = "com.juniverse.hexagonal.infrastructure.adapter.out")
@EnableTransactionManagement
public class HexagonalConfig {
    // Configuration for hexagonal architecture
    // JPA repositories are automatically scanned from the specified package
    // Transaction management is enabled for service layer
}