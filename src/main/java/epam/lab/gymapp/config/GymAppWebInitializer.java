package epam.lab.gymapp.config;

import epam.lab.gymapp.filter.perrequest.CredentialsFilter;
import epam.lab.gymapp.filter.perrequest.TransactionIdFilter;
import jakarta.servlet.*;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class GymAppWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{ApplicationConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return  null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{new CredentialsFilter(), new TransactionIdFilter()};
    }


}
