package de.hsesslingen.scpprojekt.scp.Database.Entity;

import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the Bonus Entity is correctly created
 */
@ActiveProfiles("test")
@SpringBootTest
public class BonusTest {
    private byte[] a = {100,123,23,21};
    private Image image = new Image("Laufen","png",a);
    private Date Chastartdate = new Date(2023,1,27);
    private Date Chaenddate = new Date(2023,4,27);
    private Date Bonusstartdate = new Date(2023,2,27);
    private Date Bonusenddate = new Date(2023,3,27);
    private Sport sport= new Sport("Laufen",3);
    private Challenge challenge = new Challenge("Laufen ins dritte Jahundert", "Man läuft", Chastartdate, Chaenddate, image, 2);
    private ChallengeSport challengeSport = new ChallengeSport(3,challenge,sport);
    /**
     * Test if the Bonus Entity is correctly created
     */
    @Test
    void BonusTest(){
        Bonus bonusTest = new Bonus(challengeSport,Bonusstartdate,Bonusenddate,10,"Jahresfeier","Zur Feier unseren Jahres Tages");
        assertEquals(challengeSport,bonusTest.getChallengeSport());
        assertEquals(Bonusstartdate,bonusTest.getStartDate());
        assertEquals(Bonusenddate,bonusTest.getEndDate());
        assertEquals(10, bonusTest.getFactor());
        assertEquals("Jahresfeier",bonusTest.getName());
        assertEquals("Zur Feier unseren Jahres Tages",bonusTest.getDescription());
    }
}
