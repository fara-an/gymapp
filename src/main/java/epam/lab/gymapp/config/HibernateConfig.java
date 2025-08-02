package epam.lab.gymapp.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;
@Configuration
@Profile("prod")
public class HibernateConfig {

    @Value("${hibernate.packagesToScan}")
    private String packagesToScan;

    @Value("${hibernate.show-sql}")
    private String showSql;

    @Value("${hibernate.ddl-auto}")
    private String hbm2ddlAuto;

    @Value("${hibernate.format_sql}")
    private String formatSql;

    @Value("${hibernate.current_session_context_class}")
    private String currentSessionContextClass;

    @Value("${hibernate.hbm2ddl.import_files}")
    private String importFiles;

    @Value("${hibernate.dialect}")
    private String dialect;

    private final DataSource dataSource;

    public HibernateConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan(packagesToScan);

        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.show_sql", showSql);
        hibernateProperties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
        hibernateProperties.put("hibernate.hbm2ddl.import_files", importFiles);
        hibernateProperties.put("hibernate.format_sql", formatSql);
        hibernateProperties.put("hibernate.current_session_context_class", currentSessionContextClass);
        hibernateProperties.put("hibernate.dialect",dialect);


        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager getTransactionManager(SessionFactory sf) {
        return new HibernateTransactionManager(sf);
    }

}
