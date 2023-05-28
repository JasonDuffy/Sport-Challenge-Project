package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the TeamMemberDTO is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class TeamMemberDTOTest {
    @Test
    void TeamMemberDTO_Test(){
        TeamMemberDTO teamMemberDTO = new TeamMemberDTO(1,2);
        assertEquals(1, teamMemberDTO.getTeamID());
        assertEquals(2, teamMemberDTO.getMemberID());
    }
}
