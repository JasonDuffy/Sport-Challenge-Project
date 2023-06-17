package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converters;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.*;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Services.*;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the BonusConverter is correct
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class BonusConverterTest {
    @MockBean
    BonusService bonusService;

    @Autowired
    BonusConverter bonusConverter;

    List<Bonus> bonuses ;
    List<BonusDTO> bonusDTOS ;
    BonusDTO bonusDTO;
    Bonus bonus;
    ChallengeSport challengeSport;

    @BeforeEach
    public void setup() throws AlreadyExistsException, NotFoundException {

        Sport sport = new Sport();
        sport.setId(1);

        Challenge challenge = new Challenge();
        challenge.setId(1);

        challengeSport = new ChallengeSport();
        challengeSport.setId(2);
        challengeSport.setChallenge(challenge);
        challengeSport.setSport(sport);

        bonus = new Bonus();
        bonuses = new ArrayList<>();
        bonus.setId(3);
        bonuses.add(bonus);


        bonusDTO = bonusConverter.convertEntityToDto(bonus);
        bonusDTOS = new ArrayList<>();
        bonusDTOS.add(bonusDTO);
    }
    @Test
    public void convertEntityToDtoTest(){
        BonusDTO a = bonusConverter.convertEntityToDto(bonus);
        assertEquals(3,a.getId());
    }

    @Test
    public void convertEntityListToDtoListTEST(){
        List<BonusDTO> bonusDTOS1 = bonusConverter.convertEntityListToDtoList(bonuses);
        assertEquals(3,bonusDTOS1.get(0).getId());
    }

    @Test
    public void convertDtoToEntityTEST() throws NotFoundException {
        Bonus a = bonusConverter.convertDtoToEntity(bonusDTO);
        assertEquals(3,a.getId());

    }

    @Test
    public void convertDtoListToEntityListTest() throws NotFoundException {
        List<Bonus> bonusList = bonusConverter.convertDtoListToEntityList(bonusDTOS);
        assertEquals(3,bonusList.get(0).getId());
    }
}

