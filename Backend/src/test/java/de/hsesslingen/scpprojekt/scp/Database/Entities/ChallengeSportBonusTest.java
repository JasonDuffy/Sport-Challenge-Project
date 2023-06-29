package de.hsesslingen.scpprojekt.scp.Database.Entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the ChallengeSportBonus Entity is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeSportBonusTest {
    private byte[] a = {100,123,23,21};
    private Image image = new Image("Laufen","png",a);
    private LocalDateTime Chastartdate =  LocalDateTime.of(2023,1,27, 10, 0);
    private LocalDateTime Chaenddate =  LocalDateTime.of(2023,4,27, 10, 0);
    private LocalDateTime Bonusstartdate =  LocalDateTime.of(2023,2,27, 10, 0);
    private LocalDateTime Bonusenddate =  LocalDateTime.of(2023,3,27, 10, 0);
    private Sport sport= new Sport("Laufen",3);
    private Challenge challenge = new Challenge("Laufen ins dritte Jahundert", "Man läuft", Chastartdate, Chaenddate, image, 2);
    private ChallengeSport challengeSport = new ChallengeSport(3,challenge,sport);
    @Test
    void ChallengeSportBonusTest(){
        Bonus b = new Bonus(Bonusstartdate,Bonusenddate,10.0f,"Jahresfeier","Zur Feier unseren Jahres Tages");
        ChallengeSportBonus csb = new ChallengeSportBonus(challengeSport,b);
        assertEquals(challengeSport,csb.getChallengeSport());
        assertEquals(b,csb.getBonus());
    }
}
