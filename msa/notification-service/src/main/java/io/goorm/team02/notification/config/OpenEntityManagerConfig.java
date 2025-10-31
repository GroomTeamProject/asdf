package io.goorm.team02.notification.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

/**
 * Disable OpenEntityManagerInView for long-lived SSE connections to avoid
 * holding DB connections.
 */
@Configuration
public class OpenEntityManagerConfig {

    private static final String SSE_PATH_PREFIX = "/api/sse";

    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter() {
            @Override
            protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
                String uri = request.getRequestURI();
                return uri != null && uri.startsWith(SSE_PATH_PREFIX);
            }
        };
    }
}
