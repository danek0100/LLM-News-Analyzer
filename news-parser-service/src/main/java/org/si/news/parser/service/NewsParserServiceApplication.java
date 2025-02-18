package org.si.news.parser.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main class for starting the Parsing Scheduler Service application.
 * It initializes the Spring Boot application and logs active profiles.
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
public class NewsParserServiceApplication {

    @Autowired
    private Environment env;

    /**
     * Main method for starting the Spring Boot application.
     * It handles the startup process and logs errors if any occur.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(NewsParserServiceApplication.class, args);
    }

    /**
     * Logs active Spring profiles upon application startup.
     * If no profiles are active, it logs the use of the default profile.
     */
    @PostConstruct
    public void logActiveProfiles() {
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles.length == 0) {
            log.info("No active profile. Will be used default.");
        } else {
            log.info("Active profiles: {}", String.join(", ", activeProfiles));
        }
    }
}
