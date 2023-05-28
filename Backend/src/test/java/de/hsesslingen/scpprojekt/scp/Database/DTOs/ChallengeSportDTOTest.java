package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the ChallengeSportDTO is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeSportDTOTest {
    @Test
    void ChallengeSportDTO_Test(){
        ChallengeSportDTO challengeSportDTO = new ChallengeSportDTO(10f,1,2);
        assertEquals(1,challengeSportDTO.getChallengeID());
        assertEquals(2,challengeSportDTO.getSportID());
        assertEquals(10f,challengeSportDTO.getFactor());
    }
}
