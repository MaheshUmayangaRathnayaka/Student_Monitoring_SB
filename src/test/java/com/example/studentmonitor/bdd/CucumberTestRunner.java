package com.example.studentmonitor.bdd;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("com.example.studentmonitor.bdd")
@ConfigurationParameter(key = Constants.FEATURES_PROPERTY_NAME, value = "src/test/resources/features")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "com.example.studentmonitor.bdd")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty,html:target/cucumber-reports/report.html,json:target/cucumber-reports/report.json")
public class CucumberTestRunner {
}