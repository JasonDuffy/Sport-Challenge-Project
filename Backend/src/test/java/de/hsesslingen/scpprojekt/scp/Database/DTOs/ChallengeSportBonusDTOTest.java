package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSportBonus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the ChallengeSportBonusDTO is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeSportBonusDTOTest {
    @Test
    void ChallengeSportBonusDTO_Test(){
    ChallengeSportBonusDTO csb = new ChallengeSportBonusDTO(1,2);
    assertEquals(1L,csb.getChallengeSportID());
    assertEquals(2,csb.getBonusID());
    }
}
