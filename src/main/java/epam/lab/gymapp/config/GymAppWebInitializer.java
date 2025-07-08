package epam.lab.gymapp.config;

import epam.lab.gymapp.filter.perrequest.TransactionIdFilter;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import jakarta.servlet.*;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class GymAppWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    AuthenticationService authenticationService;

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
        return new Filter[]{ new TransactionIdFilter()};
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        FilterRegistration.Dynamic authFilter = servletContext.addFilter("authFilterBean", new DelegatingFilterProxy("authFilterBean"));
        authFilter.addMappingForUrlPatterns(null, false, "/*");

    }
}
