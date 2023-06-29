package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the BonusDTO is correctly created
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class BonusDTOTest {

    LocalDateTime a = LocalDateTime.of(1000,10,10,10,10);
    LocalDateTime b = LocalDateTime.of(2000,10,10,10,10);

    @Test
    void BonusDTO_Test(){
        BonusDTO bonusDTO = new BonusDTO(1L,a,b,10f,"hi","hallo");
        assertEquals(a,bonusDTO.getStartDate());
        assertEquals(b,bonusDTO.getEndDate());
        assertEquals(10f, bonusDTO.getFactor());
        assertEquals("hi", bonusDTO.getName());
        assertEquals("hallo", bonusDTO.getDescription());
    }
}
