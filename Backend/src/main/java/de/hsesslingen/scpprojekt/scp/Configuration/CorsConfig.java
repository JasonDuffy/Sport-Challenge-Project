package de.hsesslingen.scpprojekt.scp.Configuration;

import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
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
                // Allows setting of frontend IP via environment variable
                String frontendURL = System.getenv("SCP_Frontend_URL");

                registry.addMapping("/**").allowedOrigins("http://localhost:3000", "null", frontendURL).allowedHeaders("*").allowCredentials(true).allowedMethods("*"); // Always allow localhost, null and frontend access
            }
        };
    }
}
