package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the ChallengeDTO is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeDTOTest {
    LocalDateTime a = LocalDateTime.of(1000,10,10,10,10);
    LocalDateTime b = LocalDateTime.of(2000,10,10,10,10);
    @Test
    void ChallengeDTO_Test(){
        ChallengeDTO challengeDTO = new ChallengeDTO("Challenge A","Laufen",a,b,1L,10000f);
        assertEquals(1L,challengeDTO.getImageID());
        assertEquals(a,challengeDTO.getStartDate());
        assertEquals(b,challengeDTO.getEndDate());
        assertEquals(10000f, challengeDTO.getTargetDistance());
        assertEquals("Challenge A", challengeDTO.getName());
        assertEquals("Laufen", challengeDTO.getDescription());
    }

}
