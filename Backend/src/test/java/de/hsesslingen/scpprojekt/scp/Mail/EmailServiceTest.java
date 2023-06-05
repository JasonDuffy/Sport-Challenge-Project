package de.hsesslingen.scpprojekt.scp.Mail;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import de.hsesslingen.scpprojekt.scp.Mail.Services.EmailService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

/**
 * Test of Email Service
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@SpringBootTest
public class EmailServiceTest {
    @MockBean
    private JavaMailSender sender;
    @MockBean
    ChallengeService challengeService;
    @MockBean
    ChallengeSportService challengeSportService;
    @MockBean
    MemberService memberService;

    @Autowired
    ChallengeSportConverter challengeSportConverter;
    @Autowired
    ChallengeConverter challengeConverter;
    @Autowired
    EmailService emailService;
    @Autowired
    private Environment environment;
    @Autowired
    private SpringTemplateEngine thymeleafTemplateEngine;
    @Autowired
    private ThreadPoolTaskExecutor pool; // Testing async methods

    /**
     * Test of sendHTMLMessage
     * @throws MessagingException Should never be thrown
     */
    @Test
    public void sendHTMLMessageTest() throws MessagingException {
        List<String> to = new ArrayList<>();
        to.add("test@example.com"); to.add("test2@example.com");
        String subject = "Test Mail";
        String body = "<html></html>";

        InternetAddress[] addresses = new InternetAddress[to.size()];
        addresses[0] = new InternetAddress(to.get(0));
        addresses[1] = new InternetAddress(to.get(1));

        InternetAddress fromAddress = new InternetAddress(Objects.requireNonNull(environment.getProperty("spring.mail.username")));

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(sender.createMimeMessage()).thenReturn(mockMessage);

        // Not anonymous
        emailService.sendHTMLMessage(to, subject, body, false);

        verify(sender).send(mockMessage);
        verify(mockMessage).setFrom(fromAddress);
        verify(mockMessage).setSubject(subject, "UTF-8");
        verify(mockMessage).setRecipients(Message.RecipientType.TO, addresses);

        // Anonymous
        emailService.sendHTMLMessage(to, subject, body, true);

        verify(sender, times(2)).send(mockMessage);
        verify(mockMessage, times(2)).setFrom(fromAddress);
        verify(mockMessage, times(2)).setSubject(subject, "UTF-8");
        verify(mockMessage).setRecipient(Message.RecipientType.TO, fromAddress);
        verify(mockMessage).setRecipients(Message.RecipientType.BCC, addresses);
    }

    /**
     * Test of sendHTMLMessageWithImage
     * @throws MessagingException Should never be thrown
     */
    @Test
    public void sendHTMLMessageWithImageTest() throws MessagingException {
        List<String> to = new ArrayList<>();
        to.add("test@example.com"); to.add("test2@example.com");
        String subject = "Test Mail";
        String body = "<html></html>";

        InternetAddress[] addresses = new InternetAddress[to.size()];
        addresses[0] = new InternetAddress(to.get(0));
        addresses[1] = new InternetAddress(to.get(1));

        InternetAddress fromAddress = new InternetAddress(Objects.requireNonNull(environment.getProperty("spring.mail.username")));

        Image image = new Image();
        image.setId(1L);
        image.setName("Testimage");
        image.setType("image/png");
        image.setData("12345".getBytes());

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(sender.createMimeMessage()).thenReturn(mockMessage);

        // Not anonymous
        emailService.sendHTMLMessageWithImage(to, subject, body, image.getName(), image, false);

        verify(sender).send(mockMessage);
        verify(mockMessage).setFrom(fromAddress);
        verify(mockMessage).setSubject(subject, "UTF-8");
        verify(mockMessage).setRecipients(Message.RecipientType.TO, addresses);

        // Anonymous
        emailService.sendHTMLMessageWithImage(to, subject, body, image.getName(), image, true);

        verify(sender, times(2)).send(mockMessage);
        verify(mockMessage, times(2)).setFrom(fromAddress);
        verify(mockMessage, times(2)).setSubject(subject, "UTF-8");
        verify(mockMessage).setRecipient(Message.RecipientType.TO, fromAddress);
        verify(mockMessage).setRecipients(Message.RecipientType.BCC, addresses);
    }

    /**
     * Test of sendBonusMail
     * @throws MessagingException Should never be thrown
     */
    @Test
    public void sendBonusMailTest() throws MessagingException, InterruptedException {
        Challenge challenge = new Challenge();
        challenge.setName("Test Challenge");
        challenge.setId(1L);

        Sport sport = new Sport();
        sport.setName("Test Sport");
        sport.setFactor(10.0f);

        ChallengeSport challengeSport = new ChallengeSport();
        challengeSport.setChallenge(challenge);
        challengeSport.setSport(sport);

        Bonus bonus = new Bonus();
        bonus.setChallengeSport(challengeSport);
        bonus.setName("Test Bonus");
        bonus.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        bonus.setEndDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        bonus.setFactor(10.0f);
        bonus.setDescription("Test Bonus Description");

        List<String> to = new ArrayList<>();
        to.add("test@example.com"); to.add("test2@example.com");
        when(challengeService.getChallengeMembersEmails(1L)).thenReturn(to);

        InternetAddress fromAddress = new InternetAddress(Objects.requireNonNull(environment.getProperty("spring.mail.username")));

        InternetAddress[] addresses = new InternetAddress[to.size()];
        addresses[0] = new InternetAddress(to.get(0));
        addresses[1] = new InternetAddress(to.get(1));

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(sender.createMimeMessage()).thenReturn(mockMessage);

        emailService.sendBonusMail(bonus);

        // Wait for execution to finish
        boolean awaitTermination = pool.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
        assertFalse(awaitTermination);

        verify(sender).send(mockMessage);
        verify(mockMessage).setFrom(fromAddress);
        verify(mockMessage).setSubject("Test Challenge hat einen neuen Bonus!", "UTF-8");
        verify(mockMessage).setRecipient(Message.RecipientType.TO, fromAddress);
        verify(mockMessage).setRecipients(Message.RecipientType.BCC, addresses);
    }

    /**
     * Test of sendChallengeMail
     * @throws MessagingException Should never be thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void sendChallengeMailTest() throws MessagingException, NotFoundException, InterruptedException {
        Image image = new Image();
        image.setData("123".getBytes());
        image.setName("Test Image");
        image.setType("image/png");
        image.setId(0L);

        Challenge challenge = new Challenge();
        challenge.setName("Test Challenge");
        challenge.setId(1L);
        challenge.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        challenge.setEndDate(LocalDateTime.of(2024, 1, 1, 0, 0));
        challenge.setTargetDistance(100.0f);
        challenge.setDescription("Test Challenge Description");
        challenge.setImage(image);

        Sport sport1 = new Sport();
        sport1.setId(1L);
        sport1.setFactor(10.0f);
        sport1.setName("Test Sport 1");
        Sport sport2 = new Sport();
        sport2.setId(2L);
        sport2.setFactor(20.0f);
        sport2.setName("Test Sport 2");

        ChallengeSport cs1 = new ChallengeSport();
        cs1.setSport(sport1);
        cs1.setChallenge(challenge);
        cs1.setFactor(10.0f);
        ChallengeSport cs2 = new ChallengeSport();
        cs2.setSport(sport2);
        cs2.setChallenge(challenge);
        cs2.setFactor(20.0f);
        List<ChallengeSport> challengeSportList = new ArrayList<>();
        challengeSportList.add(cs1); challengeSportList.add(cs2);

        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(challenge));
        when(challengeSportService.getAllChallengeSportsOfChallenge(1L)).thenReturn(challengeSportConverter.convertEntityListToDtoList(challengeSportList));

        List<String> to = new ArrayList<>();
        to.add("test@example.com"); to.add("test2@example.com");
        when(memberService.getAllEmails()).thenReturn(to);

        InternetAddress fromAddress = new InternetAddress(Objects.requireNonNull(environment.getProperty("spring.mail.username")));

        InternetAddress[] addresses = new InternetAddress[to.size()];
        addresses[0] = new InternetAddress(to.get(0));
        addresses[1] = new InternetAddress(to.get(1));

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(sender.createMimeMessage()).thenReturn(mockMessage);

        emailService.sendChallengeMail(challenge);

        // Wait for execution to finish
        boolean awaitTermination = pool.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
        assertFalse(awaitTermination);

        verify(sender).send(mockMessage);
        verify(mockMessage).setFrom(fromAddress);
        verify(mockMessage).setSubject("Es gibt eine neue Challenge!", "UTF-8");
        verify(mockMessage).setRecipient(Message.RecipientType.TO, fromAddress);
        verify(mockMessage).setRecipients(Message.RecipientType.BCC, addresses);
    }

    /**
     * Test of sendActivityReminder
     * @throws MessagingException Should never be thrown
     */
    @Test
    public void sendActivityReminderTest() throws MessagingException, InterruptedException {
        MemberDTO member1 = new MemberDTO();
        member1.setFirstName("Test First Name 1");
        member1.setEmail("test1@example.com");
        MemberDTO member2 = new MemberDTO();
        member2.setFirstName("Test First Name 2");
        member2.setEmail("test2@example.com");

        List<MemberDTO> memberList = new ArrayList<>();
        memberList.add(member1); memberList.add(member2);

        when(memberService.getAllMembersWhoseLastActivityWasMoreThanOneWeekAgo()).thenReturn(memberList);

        InternetAddress fromAddress = new InternetAddress(Objects.requireNonNull(environment.getProperty("spring.mail.username")));

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(sender.createMimeMessage()).thenReturn(mockMessage);

        emailService.sendActivityReminder();

        // Wait for execution to finish
        boolean awaitTermination = pool.getThreadPoolExecutor().awaitTermination(1, TimeUnit.SECONDS);
        assertFalse(awaitTermination);

        verify(sender, times(memberList.size())).send(mockMessage);
        verify(mockMessage, times(memberList.size())).setFrom(fromAddress);

        for(MemberDTO m : memberList){
            verify(mockMessage).setSubject("Wir vermissen dich, " + m.getFirstName() + " 🙁", "UTF-8");
            InternetAddress[] address = new InternetAddress[1];
            address[0] = new InternetAddress(m.getEmail());
            verify(mockMessage).setRecipients(Message.RecipientType.TO, address);
        }
    }
}
