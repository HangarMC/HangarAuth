package io.papermc.hangarauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HangarAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(HangarAuthApplication.class, args);
    }

}
