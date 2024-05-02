package org.changppo.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AccountBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountBatchApplication.class, args);
    }

}
