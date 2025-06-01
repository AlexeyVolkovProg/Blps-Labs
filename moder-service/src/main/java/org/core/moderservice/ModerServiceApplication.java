package org.core.moderservice;

import org.camunda.bpm.application.ProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@ProcessApplication
@SpringBootApplication
public class ModerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModerServiceApplication.class, args);
    }

}
