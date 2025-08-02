package epam.lab.gymapp.config;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HibernateConfigDevTest {

    private HibernateConfig config;
    private DataSource mockDataSource;

    @BeforeEach
    void setUp() {
        mockDataSource = Mockito.mock(DataSource.class);
        config = new HibernateConfig(mockDataSource);

        ReflectionTestUtils.setField(config, "packagesToScan","epam.lab.gymapp.model" );
        ReflectionTestUtils.setField(config, "showSql","true" );
        ReflectionTestUtils.setField(config, "hbm2ddlAuto", "create-drop");
        ReflectionTestUtils.setField(config, "formatSql","true" );
        ReflectionTestUtils.setField(config, "currentSessionContextClass", "org.springframework.orm.hibernate5.SpringSessionContext");
        ReflectionTestUtils.setField(config, "importFiles", "data.sql");
        ReflectionTestUtils.setField(config, "dialect","org.hibernate.dialect.PostgreSQLDialect");

    }
    @Test
    void testSessionFactoryProperties() throws Exception {
        LocalSessionFactoryBean sessionFactoryBean = config.getSessionFactory();

        assertNotNull(sessionFactoryBean);
        assertEquals(mockDataSource, ReflectionTestUtils.getField(sessionFactoryBean, "dataSource"));
        assertArrayEquals(
                new String[]{"epam.lab.gymapp.model"},
                (String[]) ReflectionTestUtils.getField(sessionFactoryBean, "packagesToScan")
        );

        Properties props = sessionFactoryBean.getHibernateProperties();
        assertEquals("true", props.getProperty("hibernate.show_sql"));
        assertEquals("create-drop", props.getProperty("hibernate.hbm2ddl.auto"));
        assertEquals("data.sql", props.getProperty("hibernate.hbm2ddl.import_files"));
        assertEquals("true", props.getProperty("hibernate.format_sql"));
        assertEquals("org.springframework.orm.hibernate5.SpringSessionContext",
                props.getProperty("hibernate.current_session_context_class"));
    }

    @Test
    void testTransactionManager() {
        SessionFactory sessionFactory = Mockito.mock(SessionFactory.class);
        HibernateTransactionManager transactionManager = config.getTransactionManager(sessionFactory);

        assertNotNull(transactionManager);
        assertEquals(sessionFactory, transactionManager.getSessionFactory());
    }
}
