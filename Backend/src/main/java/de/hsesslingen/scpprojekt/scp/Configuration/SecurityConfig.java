package de.hsesslingen.scpprojekt.scp.Configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2MetadataFilter;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.web.http.CookieSerializer;

/**
 * Security config that allows access after authentication by SAML2
 * @author Jason Patrick Duffy, baeldung (https://www.baeldung.com/spring-security-saml)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RelyingPartyRegistrationRepository relyingPartyRegistrationRepository;

    /**
     * Security chain that defines how HTTP calls are handled
     *
     * @param http HttpSecurity object that is being used
     * @return HTTP filter chain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        DefaultRelyingPartyRegistrationResolver relyingPartyRegistrationResolver = new DefaultRelyingPartyRegistrationResolver(this.relyingPartyRegistrationRepository);
        Saml2MetadataFilter filter = new Saml2MetadataFilter(relyingPartyRegistrationResolver, new OpenSamlMetadataResolver());

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/saml/login/").authenticated()
                        .anyRequest().permitAll())
                .saml2Login(withDefaults())
                .saml2Logout(withDefaults())
                .addFilterBefore(filter, Saml2WebSsoAuthenticationFilter.class);

        http.csrf().disable();

        return http.build();
    }

    /**
     * Sets SameSite attribute on all cookies to null. Needed for authentication to work on local and online network
     * @return Correctly set up serializer object
     */
    @Bean
    public DefaultCookieSerializerCustomizer cookieSerializerCustomizer() {
        return cookieSerializer -> {
            cookieSerializer.setSameSite(null);
        };
    }
}