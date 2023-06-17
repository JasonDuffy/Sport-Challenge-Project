package de.hsesslingen.scpprojekt.scp.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Class handling the configuration of CORS.
 * @author Jason Patrick Duffy
 */
@Configuration
public class CorsConfig {
    /**
     * Configures CORS project wide. Change frontendURL in addCorsMappings to URL of frontend.
     * @return Configured WebMvc
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Set to URL of frontend
                final String frontendURL = "http://localhost:3000";

                registry.addMapping("/**").allowedOrigins(frontendURL).allowedHeaders("*").allowCredentials(true).allowedMethods("*"); // Allows the frontend to access all of the server
            }
        };
    }
}
