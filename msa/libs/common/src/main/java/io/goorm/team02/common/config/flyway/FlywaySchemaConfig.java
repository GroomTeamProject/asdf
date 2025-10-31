package io.goorm.team02.common.config.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywaySchemaConfig {

    @Bean(name = "flywaySchema")
    public Flyway flywaySchema(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/schema")
                .table("flyway_schema_history")
                .baselineOnMigrate(true)
                .outOfOrder(false)
                .load();

        flyway.migrate();
        return flyway;
    }
}
