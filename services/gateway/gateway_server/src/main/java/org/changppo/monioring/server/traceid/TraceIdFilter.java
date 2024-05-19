package org.changppo.monioring.server.traceid;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceIdFilter {

    @Bean
    public TraceIdGrantFilter traceIdGrantFilter() {
        return new TraceIdGrantFilter();
    }
}
