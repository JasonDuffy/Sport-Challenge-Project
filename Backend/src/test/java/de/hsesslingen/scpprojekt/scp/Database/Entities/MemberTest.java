package de.hsesslingen.scpprojekt.scp.Database.Entities;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Member Entity Test
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class MemberTest {

    /**
     * Test if the Member Entity is correctly created
     */
    @Test
    void testMember(){
        Member membertest = new Member("maxmuster@email.com","Max","Mustermann");

        assertEquals("maxmuster@email.com",membertest.getEmail());
        assertEquals("Max",membertest.getFirstName());
        assertEquals("Mustermann",membertest.getLastName());
    }
}
