package de.hsesslingen.scpprojekt.scp.Database.Entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the Bonus Entity is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class BonusTest {
    private byte[] a = {100,123,23,21};
    private Image image = new Image("Laufen","png",a);
    private LocalDateTime Chastartdate =  LocalDateTime.of(2023,1,27, 10, 0);
    private LocalDateTime Chaenddate =  LocalDateTime.of(2023,4,27, 10, 0);
    private LocalDateTime Bonusstartdate =  LocalDateTime.of(2023,2,27, 10, 0);
    private LocalDateTime Bonusenddate =  LocalDateTime.of(2023,3,27, 10, 0);
    private Sport sport= new Sport("Laufen",3);
    private Challenge challenge = new Challenge("Laufen ins dritte Jahundert", "Man läuft", Chastartdate, Chaenddate, image, 2);
    /**
     * Test if the Bonus Entity is correctly created
     */
    @Test
    void BonusTest(){
        Bonus bonusTest = new Bonus(Bonusstartdate,Bonusenddate,10.0f,"Jahresfeier","Zur Feier unseren Jahres Tages");
        assertEquals(Bonusstartdate,bonusTest.getStartDate());
        assertEquals(Bonusenddate,bonusTest.getEndDate());
        assertEquals(10, bonusTest.getFactor());
        assertEquals("Jahresfeier",bonusTest.getName());
        assertEquals("Zur Feier unseren Jahres Tages",bonusTest.getDescription());
    }
}
