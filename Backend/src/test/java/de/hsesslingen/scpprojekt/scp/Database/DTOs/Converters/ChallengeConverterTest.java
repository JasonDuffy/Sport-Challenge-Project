package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converters;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamService;
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
 * Test if the ChallengeConverter is correct
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeConverterTest {
    @MockBean
    ChallengeService challengeService;

    @Autowired
    ChallengeConverter challengeConverter;
    List<Challenge> challengeList ;
    List<ChallengeDTO> challengeDTOList ;
    ChallengeDTO challengeDTO;
    Challenge challenge;

    @BeforeEach
    public void setup() throws AlreadyExistsException, NotFoundException {

        Image image = new Image();
        image.setId(2L);

        challenge = new Challenge();
        challenge.setId(1);
        challenge.setImage(image);

        challengeList = new ArrayList<>();
        challengeList.add(challenge);

        challengeDTO = challengeConverter.convertEntityToDto(challenge);
        challengeDTOList = new ArrayList<>();
        challengeDTOList.add(challengeDTO);
    }
    @Test
    public void convertEntityToDtoTest(){
        ChallengeDTO a = challengeConverter.convertEntityToDto(challenge);
        assertEquals(1,a.getId());
        assertEquals(2,a.getImageID());
    }

    @Test
    public void convertEntityListToDtoListTEST(){
        List<ChallengeDTO> challengeDTOS = challengeConverter.convertEntityListToDtoList(challengeList);
        assertEquals(2,challengeDTOS.get(0).getImageID());
        assertEquals(1,challengeDTOS.get(0).getId());
    }

    @Test
    public void convertDtoToEntityTEST() throws NotFoundException {
        Challenge a = challengeConverter.convertDtoToEntity(challengeDTO);
        assertEquals(1,a.getId());
        assertEquals(2L,a.getImage().getId());
    }

    @Test
    public void convertDtoListToEntityListTest() throws NotFoundException {
        List<Challenge> challenges = challengeConverter.convertDtoListToEntityList(challengeDTOList);
        assertEquals(1,challenges.get(0).getId());
        assertEquals(2L,challenges.get(0).getImage().getId());
    }
}

