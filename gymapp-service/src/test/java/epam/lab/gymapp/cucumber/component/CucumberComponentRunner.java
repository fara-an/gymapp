package epam.lab.gymapp.cucumber.component;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("cucumber/features/component")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "epam.lab.gymapp.cucumber.component"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, json:target/cucumber-reports/Cucumber.json"
)
@ConfigurationParameter(
        key = "cucumber.experimental.exclude-configurations",
        value = "true"
)
public class CucumberComponentRunner {
}
