package de.hsesslingen.scpprojekt.scp.Mail.Configuration;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * Sets up the directory for the mail templates
 * <a href="https://www.baeldung.com/spring-email-templates">See here</a> for more information.
 * @author Jason Patrick Duffy
 */
@Configuration
public class MailConfiguration {
    /**
     * Allows loadiing of templates from resources/mail-templates/
     * @return Complete TemplateResolver
     */
    @Bean
    @Primary
    public ITemplateResolver thymeleafTemplateResolver(){
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("mail-templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        templateResolver.setCheckExistence(true);
        return templateResolver;
    }

    /**
     * Sets up the ThymeleafTemplateEngine
     * @param templateResolver TemplateResolver that should be used
     * @return Complete SpringTemplateEngine
     */
    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver templateResolver){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
}
