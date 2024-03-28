package org.changppo.cost_management_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CostManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CostManagementServiceApplication.class, args);
	}

}
