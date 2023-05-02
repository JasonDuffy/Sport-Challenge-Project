package de.hsesslingen.scpprojekt.scp.Database.Entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

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
    private LocalDate startdate =  LocalDate.of(2023,1,27);
    private LocalDate enddate = LocalDate.of(2023, 4, 27);
    private Sport sport= new Sport("Laufen",3);
    private Challenge challenge = new Challenge("Laufen ins dritte Jahundert", "Man läuft", startdate, enddate, image, 2);

    private  ChallengeSport challengeSport = new ChallengeSport(3,challenge,sport);
    private  Member member = new Member("doofen@email.com","Doof","EnSChmert");
    /**
     * Test if the Activity Entity is correctly created
     */
    @Test
    void ActivityTest(){
        Activity activityTest = new Activity(challengeSport,member,10);
        assertEquals(challengeSport,activityTest.getChallengeSport());
        assertEquals(member,activityTest.getMember());
        assertEquals(10,activityTest.getDistance());
    }
}
