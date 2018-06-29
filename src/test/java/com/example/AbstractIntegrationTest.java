package com.example;

import java.io.File;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.DockerComposeContainer;

import com.example.demo.DemoApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApplication.class})
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
// As Spring does context caching when we run multiple tests, we need to clean the context after each test class to be able to connect to the right mongo container
@DirtiesContext
public abstract class AbstractIntegrationTest {
    private static final String MONGO_CONTAINER_NAME = "mongodb";
    private static final String MONGO_HOST = "localhost";
    private static final int MONGO_PORT = 27017;
    // With this rule we start a new container for each test class.
    @ClassRule
    public static DockerComposeContainer dockerComposeContainer =
            new DockerComposeContainer(new File("src/test/resources/docker-compose-mongo.yml"))
                    .withExposedService( MONGO_CONTAINER_NAME, MONGO_PORT);

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            final ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
            String mongoHost = String.format("mongodb.host=%s", MONGO_HOST);
            String mongoPort = String.format("mongodb.port=%s", dockerComposeContainer.getServicePort(MONGO_CONTAINER_NAME, MONGO_PORT));
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(environment, mongoHost, mongoPort);
        }
    }
}
