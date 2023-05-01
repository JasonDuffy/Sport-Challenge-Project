package de.hsesslingen.scpprojekt.scp.Database.Entity;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the ChallengeSport Entity is correctly created
 *
 * @author  Tom Nguyen Dinh
 */

@ActiveProfiles("test")
@SpringBootTest
public class ChallengeSportTest {
    private byte[] a = {100,123,23,21};
    private Image image = new Image("Laufen","png",a);
    private LocalDate startdate =  LocalDate.of(2023,1,27);
    private LocalDate enddate =  LocalDate.of(2023,4,27);
    private  Sport sport= new Sport("Laufen",3);
    private Challenge challenge = new Challenge("Laufen ins dritte Jahundert", "Man läuft", startdate, enddate, image, 2);
    /**
     * Test if the ChallengeSport Entity is correctly created
     */

    @Test
    void ChallengeSportTest(){
        ChallengeSport challengeSportTest = new ChallengeSport(3,challenge,sport);
        assertEquals(3,challengeSportTest.getFactor());
        assertEquals(challenge,challengeSportTest.getChallenge());
        assertEquals(sport,challengeSportTest.getSport());
    }

}
