package de.hsesslingen.scpprojekt.scp.Database.Entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Challenge Entity Test
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeTest {
    private byte[] a = {100,123,23,21};
    private Image image = new Image("Laufen","png",a);
    private LocalDate startdate = LocalDate.of(2023,1,27);
    private LocalDate enddate =  LocalDate.of(2023,4,27);
    /**
     * Test if the Member Entity is correctly created
     */
    @Test
    void ChallengeTest() {
        Challenge challengeTest = new Challenge("Laufen", "Man läuft", startdate, enddate, image, 2);
        assertEquals("Laufen", challengeTest.getName());
        assertEquals("Man läuft", challengeTest.getDescription());
        assertEquals(startdate, challengeTest.getStartDate());
        assertEquals(enddate, challengeTest.getEndDate());
        assertEquals(image, challengeTest.getImage());
        assertEquals(2, challengeTest.getTargetDistance());
    }
}
