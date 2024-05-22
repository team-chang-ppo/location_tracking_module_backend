package org.changppo.monitoring.security;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;

import java.io.IOException;

@Slf4j
public class ErrorStateDebugFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Unhandled error occurred", e);
        }
        if (response instanceof Response responseConnector) {
            boolean errorReportRequired = responseConnector.isErrorReportRequired();
            if (errorReportRequired) {
                log.error("{} error report required, coyoteResponse's errorState is NOT_REPORTED", Response.class.getName());
            }
        }
    }
}
