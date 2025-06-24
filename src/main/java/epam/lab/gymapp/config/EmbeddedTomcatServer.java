package epam.lab.gymapp.config;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;


public class EmbeddedTomcatServer {
    public static void main(String[] args) throws LifecycleException {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        String docBase = new java.io.File(".").getAbsolutePath();
        var ctx = tomcat.addWebapp("", docBase);
        ctx.addApplicationListener("epam.lab.gymapp.config.GymAppWebInitializer");


        tomcat.start();
        tomcat.getServer().await();


    }
}
