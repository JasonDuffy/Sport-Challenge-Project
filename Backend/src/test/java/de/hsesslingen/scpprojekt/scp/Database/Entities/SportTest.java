package de.hsesslingen.scpprojekt.scp.Database.Entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Sport Entity Test
 * @author Tom Nguyen Dinh
 */
public class SportTest {
    /**
     * Test if the Sport is correctly created
     */

    @Test
    void testSport(){
        Sport sportTest= new Sport("Laufen",3);
        assertEquals("Laufen",sportTest.getName());
        assertEquals(3,sportTest.getFactor());
    }
}
