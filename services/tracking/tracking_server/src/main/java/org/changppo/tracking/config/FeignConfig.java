package org.changppo.tracking.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "org.changppo.tracking")
@Configuration
public class FeignConfig {
}
