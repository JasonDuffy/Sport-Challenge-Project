package de.hsesslingen.scpprojekt.scp.Database.Entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TeamMember Entity Test
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class TeamMemberTest {

    private byte[] aChallenge = {100,123,124};
    private byte[] bTeam = {10,11,12};
    private Image imageChallenge = new Image("Challenge laufen","PNG",aChallenge);
    private Image imageTeam = new Image("Laufen", "PNG",bTeam);
    private LocalDateTime startdate =  LocalDateTime.of(2023,1,27, 10, 0);
    private LocalDateTime enddate =  LocalDateTime.of(2023,4,27, 10, 0);
    private Challenge challenge = new Challenge("Laufen", "Man läuft", startdate, enddate, imageChallenge, 2);
    private Team team =  new Team("Rasender",imageTeam, challenge);
    private Member member =new Member("JaxL@email.com","Jax","Laterne");

    /**
     * Test if the TeamMember is correctly created
     */
    @Test
    void testTeamMember(){
        TeamMember teamMemberTest = new TeamMember(team,member);
        assertEquals(team,teamMemberTest.getTeam());
        assertEquals(member,teamMemberTest.getMember());


    }
}
