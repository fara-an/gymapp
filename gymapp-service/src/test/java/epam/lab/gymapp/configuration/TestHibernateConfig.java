package epam.lab.gymapp.configuration;

import org.hibernate.SessionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@TestConfiguration
public class TestHibernateConfig {

    @Bean
    @Primary
    public SessionFactory testSessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPackagesToScan("epam.lab.gymapp.model");

        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.show_sql", "true");

        factoryBean.setHibernateProperties(props);

        try {
            factoryBean.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return factoryBean.getObject();
    }


    @Bean
    public HibernateTransactionManager getTransactionManager(SessionFactory sf) {
        return new HibernateTransactionManager(sf);
    }
}
