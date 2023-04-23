package de.hsesslingen.scpprojekt.scp.Authentication;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SAML2User class tests
 * @author Jason Patrick Duffy
 */

@SpringBootTest
public class SAML2UserTest {

    /**
     * Tests if the SAML2User class is generated correctly
     */
    @Test
    void testSAML2User(){
        SAML2User testUser = new SAML2User("max@example.com", "Max Emilian", "Mustermann");

        assertEquals("max@example.com", testUser.getEmail());
        assertEquals("Max Emilian", testUser.getFirstName());
        assertEquals("Mustermann", testUser.getLastName());
    }
}
