package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the ActivityDTO is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ActivityDTOTest {

    LocalDateTime a = LocalDateTime.of(1000,10,10,10,10);
    @Test
    void ActivityDTO_Test(){
        ActivityDTO activityTest = new ActivityDTO(1,1,2,10f,a);
        assertEquals(1,activityTest.getChallengeSportID());
        assertEquals(2,activityTest.getMemberID());
        assertEquals(10,activityTest.getDistance());
        assertEquals(a,activityTest.getDate());
    }
}
