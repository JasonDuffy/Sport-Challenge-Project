package de.hsesslingen.scpprojekt.scp;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Mail.Services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Class for tasks that run on a schedule
 * @author Jason Patrick Duffy
 */
@Component
public class ScheduledTasks {
    @Autowired
    @Lazy
    EmailService emailService;

    /**
     * Sends an email to all members whose last activity was more than one week ago
     * Every friday at 12 PM
     */
    @Scheduled(cron = "0 0 12 * * FRI") //Runs every Friday at 12 PM
    public void sendMailToInactiveUsers(){
        try {
            emailService.sendActivityReminder();
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
            System.out.println("Could not message inactive members!");
        }
    }
}
