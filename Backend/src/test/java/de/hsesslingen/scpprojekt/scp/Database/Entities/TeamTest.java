package de.hsesslingen.scpprojekt.scp.Database.Entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Challenge Entity Test
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class TeamTest {
    private byte[] aChallenge = {100,123,124};
    private byte[] bTeam = {10,11,12};
    private Image imageChallenge = new Image("Challenge laufen","PNG",aChallenge);
    private Image imageTeam = new Image("Laufen", "PNG",bTeam);
    private LocalDateTime startdate =  LocalDateTime.of(2023,1,27, 10, 0);
    private LocalDateTime enddate =  LocalDateTime.of(2023,4,27, 10, 0);
    private Challenge challenge = new Challenge("Laufen", "Man läuft", startdate, enddate, imageChallenge, 2);
    /**
     * Test if the Team  is correctly created
     */
    @Test
    void TeamTest(){
        Team teamTest = new Team("HasenHüppfer",imageTeam, challenge);
        assertEquals("HasenHüppfer",teamTest.getName());
        assertEquals(imageTeam,teamTest.getImage());
        assertEquals(challenge,teamTest.getChallenge());
    }

}
