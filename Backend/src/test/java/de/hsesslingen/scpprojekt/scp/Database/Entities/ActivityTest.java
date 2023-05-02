package de.hsesslingen.scpprojekt.scp.Database.Entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the Activity Entity is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ActivityTest {
    private byte[] a = {100,123,23,21};
    private Image image = new Image("Laufen","png",a);
    private LocalDateTime startdate =  LocalDateTime.of(2023,1,27, 10, 0);
    private LocalDateTime enddate = LocalDateTime.of(2023, 4, 27, 10, 0);
    private LocalDateTime activityDate = LocalDateTime.of(2023, 4, 27, 10, 0);
    private Sport sport= new Sport("Laufen",3);
    private Challenge challenge = new Challenge("Laufen ins dritte Jahundert", "Man läuft", startdate, enddate, image, 2);

    private  ChallengeSport challengeSport = new ChallengeSport(3,challenge,sport);
    private  Member member = new Member("doofen@email.com","Doof","EnSChmert");
    /**
     * Test if the Activity Entity is correctly created
     */
    @Test
    void ActivityTest(){
        Activity activityTest = new Activity(challengeSport,member,10, activityDate);
        assertEquals(challengeSport,activityTest.getChallengeSport());
        assertEquals(member,activityTest.getMember());
        assertEquals(10,activityTest.getDistance());
    }
}
