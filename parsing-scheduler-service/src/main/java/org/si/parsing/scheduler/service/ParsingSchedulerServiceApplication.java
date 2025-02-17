package org.si.parsing.scheduler.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for starting the Parsing Scheduler Service application.
 * It initializes the Spring Boot application and logs active profiles.
 */
@SpringBootApplication
@EnableScheduling
public class ParsingSchedulerServiceApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParsingSchedulerServiceApplication.class);

    @Autowired
    private Environment env;

    /**
     * Main method for starting the Spring Boot application.
     * It handles the startup process and logs errors if any occur.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ParsingSchedulerServiceApplication.class, args);
    }

    /**
     * Logs active Spring profiles upon application startup.
     * If no profiles are active, it logs the use of the default profile.
     */
    @PostConstruct
    public void logActiveProfiles() {
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length == 0) {
            LOGGER.info("No active profile. Will be used default.");
        } else {
            LOGGER.info("Active profiles: {}", String.join(", ", activeProfiles));
        }
    }
}
