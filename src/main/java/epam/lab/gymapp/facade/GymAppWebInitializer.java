package epam.lab.gymapp.facade;

import epam.lab.gymapp.config.ApplicationConfig;
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
}
