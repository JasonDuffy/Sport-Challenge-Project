package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the MemberDTO is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class MemberDTOTest {
    @Test
    void MemberDTO_Test(){
        MemberDTO memberDTO = new MemberDTO("max@jemamd.de","max","Mustermann",1L,2L, false);
        assertEquals("max@jemamd.de", memberDTO.getEmail());
        assertEquals("max", memberDTO.getFirstName());
        assertEquals("Mustermann", memberDTO.getLastName());
        assertEquals(1, memberDTO.getUserID());
        assertEquals(2L, memberDTO.getImageID());
        assertEquals(false, memberDTO.getCommunication());
    }
}
