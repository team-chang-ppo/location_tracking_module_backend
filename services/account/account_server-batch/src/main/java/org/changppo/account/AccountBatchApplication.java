package org.changppo.account;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class AccountBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountBatchApplication.class, args);
    }

    @PostConstruct
    public void initializeTimeZone(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
