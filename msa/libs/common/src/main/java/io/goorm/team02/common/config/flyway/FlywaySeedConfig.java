package io.goorm.team02.common.config.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
@Profile({ "local", "dev", "test" })
public class FlywaySeedConfig {

    @Bean
    @DependsOn("flywaySchema")  
    public Flyway flywaySeed(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration/seed")
                .table("flyway_seed_history")
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .load();

        flyway.migrate();
        return flyway;
    }
}
