package epam.lab.gymapp.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DatabaseConfigTest {

    @Nested
    @ExtendWith(SpringExtension.class)
    @ActiveProfiles("prod")
    class ProductionConfigTest {
        private final DatabaseConfig databaseConfig = new DatabaseConfig();

        {
            ReflectionTestUtils.setField(databaseConfig, "driverClassName", "org.h2.Driver");
            ReflectionTestUtils.setField(databaseConfig, "jdbcUrl", "jdbc:h2:mem:prodDb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
            ReflectionTestUtils.setField(databaseConfig, "jdbcUsername", "prod_user");
            ReflectionTestUtils.setField(databaseConfig, "jdbcPassword", "prod_pass");
        }

        @Test
        void testProdDataSource() {
            DataSource dataSource = databaseConfig.getDataSource();

            assertNotNull(dataSource, "DataSource should not be null");
            assertInstanceOf(HikariDataSource.class, dataSource, "Should be HikariDataSource in production");
            
            try (HikariDataSource hikariDataSource = (HikariDataSource) dataSource) {
                assertEquals("org.h2.Driver", hikariDataSource.getDriverClassName());
                assertEquals("jdbc:h2:mem:prodDb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", hikariDataSource.getJdbcUrl());
                assertEquals("prod_user", hikariDataSource.getUsername());
                assertEquals("prod_pass", hikariDataSource.getPassword());
            }
        }
    }



    @Nested
    @ExtendWith(SpringExtension.class)
    @ActiveProfiles("test")
    class TestConfigTest {
        private final DatabaseConfig databaseConfig = new DatabaseConfig();

        {
            ReflectionTestUtils.setField(databaseConfig, "driverClassName", "org.h2.Driver");
            ReflectionTestUtils.setField(databaseConfig, "jdbcUrl", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
            ReflectionTestUtils.setField(databaseConfig, "jdbcUsername", "test_user");
            ReflectionTestUtils.setField(databaseConfig, "jdbcPassword", "");
        }

        @Test
        void testTestDataSource() {
            DataSource dataSource = databaseConfig.getDataSource();

            assertNotNull(dataSource, "DataSource should not be null in test profile");
            assertInstanceOf(HikariDataSource.class, dataSource, "Should be HikariDataSource in test profile");
            
            try (HikariDataSource hikariDataSource = (HikariDataSource) dataSource) {
                assertEquals("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", hikariDataSource.getJdbcUrl());
                assertEquals("test_user", hikariDataSource.getUsername());
            }
        }
    }
}
