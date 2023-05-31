package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converters;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportBonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportBonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportBonusService;
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
 * Test if the ChallengeSportBonusConverter is correct
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeSportBonusConverterTest {
    @MockBean
    ChallengeSportBonusService challengeSportBonusService;
    @Autowired
    ChallengeSportBonusConverter challengeSportBonusConverter;

    List<ChallengeSportBonus> challengeSportBonusList ;
    List<ChallengeSportBonusDTO> challengeSportBonusDTOList ;
    ChallengeSportBonusDTO challengeSportBonusDTO;
    ChallengeSportBonus challengeSportBonus;

    ChallengeSport challengeSport;
    Bonus bonus;

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
        bonus.setChallengeSport(challengeSport);
        bonus.setId(1);

        challengeSportBonus = new ChallengeSportBonus();
        challengeSportBonus.setId(3);
        challengeSportBonus.setBonus(bonus);
        challengeSportBonus.setChallengeSport(challengeSport);

        challengeSportBonusList = new ArrayList<>();
        challengeSportBonusList.add(challengeSportBonus);

        challengeSportBonusDTO = challengeSportBonusConverter.convertEntityToDto(challengeSportBonus);
        challengeSportBonusDTOList = new ArrayList<>();
        challengeSportBonusDTOList.add(challengeSportBonusDTO);
    }

    @Test
    public void convertEntityToDtoTest(){
        ChallengeSportBonusDTO a = challengeSportBonusConverter.convertEntityToDto(challengeSportBonus);
        assertEquals(1,a.getBonusID());
        assertEquals(2,a.getChallengeSportID());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertEntityListToDtoListTEST(){
        List<ChallengeSportBonusDTO> a = challengeSportBonusConverter.convertEntityToDtoList(challengeSportBonusList);
        assertEquals(1,a.get(0).getBonusID());
        assertEquals(2,a.get(0).getChallengeSportID());
        assertEquals(3,a.get(0).getId());
    }

    @Test
    public void convertDtoToEntityTEST() throws NotFoundException {
        ChallengeSportBonus a = challengeSportBonusConverter.convertDtoToEntity(challengeSportBonusDTO);
        assertEquals(1,a.getBonus().getId());
        assertEquals(2,a.getChallengeSport().getId());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertDtoListToEntityListTest() throws NotFoundException {
        List<ChallengeSportBonus> a = challengeSportBonusConverter.convertDtoToEntityList(challengeSportBonusDTOList);
        assertEquals(1,a.get(0).getBonus().getId());
        assertEquals(2,a.get(0).getChallengeSport().getId());
        assertEquals(3,a.get(0).getId());
    }

}
