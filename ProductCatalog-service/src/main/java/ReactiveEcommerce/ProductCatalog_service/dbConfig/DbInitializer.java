package ReactiveEcommerce.ProductCatalog_service.dbConfig;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.util.logging.Logger;

@Configuration
public class DbInitializer extends AbstractR2dbcConfiguration {

    private Logger logger = Logger.getLogger(DbInitializer.class.getName());

    @Value("${spring.r2dbc.url}")
    private String dbUrl;

    @Value("${spring.r2dbc.initialize-schema:false}")
    private boolean initializeSchema;

    @PostConstruct
    private void validateDbUrl() {
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new IllegalArgumentException("Database URL cannot be null or empty");
        }
        System.out.println("✅ Database URL validated: " + dbUrl);
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(dbUrl);
    }

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        if (initializeSchema){
            CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
            populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
            initializer.setDatabasePopulator(populator);
        } else {
            logger.info("Database schema initialization is disabled.");
        }

        return initializer;
    }
}
