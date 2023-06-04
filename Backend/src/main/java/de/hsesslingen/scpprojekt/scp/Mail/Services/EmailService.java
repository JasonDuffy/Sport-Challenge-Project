package de.hsesslingen.scpprojekt.scp.Mail.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Autowired
    @Lazy
    ChallengeService challengeService;
    @Autowired
    @Lazy
    ChallengeSportService challengeSportService;
    @Autowired
    @Lazy
    ChallengeSportConverter challengeSportConverter;
    @Autowired
    @Lazy
    MemberService memberService;

    /**
     * Sends a HTML message to multiple recipient without them knowing of each other
     * @param to List of email addresses that should be notified
     * @param subject Subject of the email
     * @param htmlBody HTML Body of the email
     * @param anonymous Should the recipient addresses be exposed?
     * @throws MessagingException Thrown by MimeMessageHelper
     */
    public void sendHTMLMessage(List<String> to, String subject, String htmlBody, Boolean anonymous) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(Objects.requireNonNull(environment.getProperty("spring.mail.username")));

        // Hide recipient email addresses when anonymous is true
        if(anonymous){
            helper.setTo(Objects.requireNonNull(environment.getProperty("spring.mail.username")));
            helper.setBcc(convertListToArray(to)); // Hide emails from each other
        }
        else{
            helper.setTo(Objects.requireNonNull(convertListToArray(to)));
        }

        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        sender.send(message);
    }

    /**
     * Sends an HTML message with an image embedded
     * @param to List of email addresses that should be notified
     * @param subject Subject of the email
     * @param htmlBody HTML Body of the email
     * @param imageName Name of the image
     * @param image Image entity to send
     * @param anonymous Should the recipient addresses be exposed?
     * @throws MessagingException Thrown by MimeMessageHelper
     */
    public void sendHTMLMessageWithImage(List<String> to, String subject, String htmlBody, String imageName, Image image, Boolean anonymous) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(Objects.requireNonNull(environment.getProperty("spring.mail.username")));

        // Hide recipient email addresses when anonymous is true
        if(anonymous){
            helper.setTo(Objects.requireNonNull(environment.getProperty("spring.mail.username")));
            helper.setBcc(convertListToArray(to)); // Hide emails from each other
        }
        else{
            helper.setTo(Objects.requireNonNull(convertListToArray(to)));
        }

        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addInline(imageName, new ByteArrayResource(image.getData()), image.getType());

        sender.send(message);
    }

    /**
     * Sends a bonus notification email to all members of a challenge
     * @param bonus Bonus that members of the challenge should be notified about
     * @throws MessagingException Thrown by sendHTMLMessage
     */
    @Async
    public void sendBonusMail(Bonus bonus) throws MessagingException {
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("challengeName", bonus.getChallengeSport().getChallenge().getName());
        mailMap.put("bonusName", bonus.getName());
        mailMap.put("startTime", bonus.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        mailMap.put("endTime", bonus.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        mailMap.put("factor", bonus.getFactor());
        mailMap.put("description", bonus.getDescription());
        mailMap.put("sport", bonus.getChallengeSport().getSport().getName());

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(mailMap);

        List<String> to = challengeService.getChallengeMembersEmails(bonus.getChallengeSport().getChallenge().getId());
        String subject = mailMap.get("challengeName") + " hat einen neuen Bonus!";
        String htmlBody = thymeleafTemplateEngine.process("mail-bonus-template.html", thymeleafContext);

        sendHTMLMessage(to, subject, htmlBody, true);
    }

    /**
     * Notifies all members of a new challenge by email
     * @param challenge Challenge the members should be notified about
     * @throws MessagingException Thrown by sendHTMLMessage
     * @throws NotFoundException Thrown by ChallengeSportConverter
     */
    @Async
    public void sendChallengeMail(Challenge challenge) throws MessagingException, NotFoundException {
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("challengeName", challenge.getName());
        mailMap.put("description", challenge.getDescription());
        mailMap.put("startTime", challenge.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        mailMap.put("endTime", challenge.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        mailMap.put("target", challenge.getTargetDistance());

        Image image = challenge.getImage();
        mailMap.put("imageResource", image.getName());

        List<ChallengeSport> challengeSportList = challengeSportConverter.convertDtoListToEntityList(challengeSportService.getAllChallengeSportsOfChallenge(challenge.getId()));
        StringBuilder sports = new StringBuilder(challengeSportList.get(0).getSport().getName() + " (Faktor: " + challengeSportList.get(0).getFactor() + ")");
        for(int i = 1; i < challengeSportList.size(); i++){
            sports.append(", ").append(challengeSportList.get(i).getSport().getName()).append("(Faktor: ").append(challengeSportList.get(i).getFactor()).append(")");
        }
        mailMap.put("sports", sports);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(mailMap);

        List<String> to = memberService.getAllEmails();
        String subject = "Es gibt eine neue Challenge!";
        String htmlBody = thymeleafTemplateEngine.process("mail-challenge-template.html", thymeleafContext);

        sendHTMLMessageWithImage(to, subject, htmlBody, image.getName(), image, true);
    }

    /**
     * Sends a reminder to inactive members (last activity was more than 1 week ago)
     * @throws MessagingException Thrown by sendHTMLMessage
     */
    @Async
    public void sendActivityReminder() throws MessagingException {
        List<MemberDTO> inactiveMembers = memberService.getAllMembersWhoseLastActivityWasMoreThanOneWeekAgo();

        for(MemberDTO member : inactiveMembers){
            Map<String, Object> mailMap = new HashMap<>();
            mailMap.put("memberFirstName", member.getFirstName());

            Context thymeleafContext = new Context();
            thymeleafContext.setVariables(mailMap);

            List<String> to = new ArrayList<>();
            to.add(member.getEmail());

            String subject = "Wir vermissen dich, " + mailMap.get("memberFirstName") + " 🙁";
            String htmlBody = thymeleafTemplateEngine.process("mail-reminder-template.html", thymeleafContext);

            sendHTMLMessage(to, subject, htmlBody, false);
        }
    }

    /**
     * Converts a given list to an array
     * @param list List to be converted
     * @return List as array
     */
    private String[] convertListToArray(List<String> list){
        String[] returnArr = new String[list.size()];
        list.toArray(returnArr);

        return returnArr;
    }
}
