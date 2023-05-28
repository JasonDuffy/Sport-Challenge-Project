package de.hsesslingen.scpprojekt.scp.Mail.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class that allows messages to be sent
 *
 * @author Jason Patrick Duffy
 */
@Service
public class EmailService {
    @Autowired
    private Environment environment;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;

    /**
     * Sends a HTML message to multiple recipient without them knowing of each other
     * @param to List of MemberDTO objects that should be notified
     * @param subject Subject of the email
     * @param htmlBody HTML Body of the email
     * @throws MessagingException Thrown by MimeMessageHelper
     */
    public void sendHTMLMessage(List<MemberDTO> to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(Objects.requireNonNull(environment.getProperty("spring.mail.username")));
        helper.setTo(Objects.requireNonNull(environment.getProperty("spring.mail.username")));
        helper.setBcc(getEmails(to)); // Hide emails from each other
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        sender.send(message);
    }

    /**
     * Sends a bonus notification email to a list of recipients
     * @param to List of MemberDTO objects that should be notified
     * @param subject Subject of the email
     * @param templateModel Map of the Thymeleaf variables that should be placed in the template
     * @throws MessagingException Thrown by sendHTMLMessage
     */
    public void sendBonusMail(
            List<MemberDTO> to, String subject, Map<String, Object> templateModel)
            throws MessagingException {

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String htmlBody = thymeleafTemplateEngine.process("mail-bonus-template.html", thymeleafContext);

        sendHTMLMessage(to, subject, htmlBody);
    }

    /**
     * Extracts the email addresses of a given MemberDTO list and returns it
     * @param members MemberDTO list that should be extracted
     * @return Array of email addresses
     */
    private String[] getEmails(List<MemberDTO> members){
        String[] emails = new String[members.size()];

        for (int i = 0; i < members.size(); i++){
            emails[i] = members.get(i).getEmail();
        }

        return emails;
    }
}
