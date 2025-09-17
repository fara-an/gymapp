package epam.lab.gymapp.cucumber.config;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")   // looks under src/test/resources/features
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "epam.lab.gymapp.cucumber.steps"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, json:target/cucumber-reports/Cucumber.json"
)
public class RunCucumberTest {
    // Empty: all config via annotations
}
