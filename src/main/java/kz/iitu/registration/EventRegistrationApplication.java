package kz.iitu.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. @SpringBootApplication turns on auto-configuration and
 * component scanning of this package and its sub-packages, so Spring finds
 * the controller and the service and creates them as beans.
 */
@SpringBootApplication
public class EventRegistrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventRegistrationApplication.class, args);
    }
}
