package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the TeamDTO is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class TeamDTOTest {
    @Test
    void TeamDTO_Test(){
        TeamDTO teamDTO = new TeamDTO("Team Rot",1L,2);
        assertEquals("Team Rot", teamDTO.getName());
        assertEquals(1L, teamDTO.getImageID());
        assertEquals(2L, teamDTO.getChallengeID());
    }
}
