package org.example.firstlabis;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
@RequiredArgsConstructor
public class FirstLabIsApplication {

    public static void main(String[] args) throws JsonProcessingException {
        SpringApplication.run(FirstLabIsApplication.class, args);
    }
}
