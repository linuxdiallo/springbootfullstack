package com.codepro.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author aboubacar.diallo
 * @created 27/07/2023 - 16:20
 * @project springbootfullstack
 * @package com.codepro.config
 */

@Configuration
public class CorsConfig {

    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Value("#{'${cors.allowed-methods}'.split(',')}")
    private List<String> allowedMethods;

    @Value("#{'${cors.allowed-headers}'.split(',')}")
    private List<String> allowedHeaders;

    @Value("#{'${cors.exposed-headers}'.split(',')}")
    private List<String> exposeHeaders;

/* @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/api/**");
        allowedOrigins.forEach(corsRegistration::allowedOrigins);
        allowedMethods.forEach(corsRegistration::allowedMethods);
    }*/

    // we added CorsConfigurationSource because we use springSecurity and spring security checks
    // Cors definition at the first entry before the request goes to the MVC
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setExposedHeaders(exposeHeaders);
        configuration.setMaxAge(Duration.of(1, ChronoUnit.HOURS));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }


}
