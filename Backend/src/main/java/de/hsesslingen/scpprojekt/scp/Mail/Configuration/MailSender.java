package de.hsesslingen.scpprojekt.scp.Mail.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Objects;
import java.util.Properties;

/**
 * Configures the mail sender for sending mail
 *
 * @author Jason Patrick Duffy
 */
public class MailSender {
    @Autowired
    private Environment environment;

    /**
     * Sets up the email sender service
     * @return Complete JavaMailSender object
     */
    @Bean
    public JavaMailSender getJavaMailSender(){
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(environment.getProperty("spring.mail.host"));
        sender.setPort(Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.mail.port"))));

        sender.setUsername(environment.getProperty("spring.mail.username"));
        sender.setPassword(environment.getProperty("spring.mail.password"));

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));

        return sender;
    }
}
