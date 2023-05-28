package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converters;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Database.Services.SportService;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamService;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Test if the ChallengeConverter is correct
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeSportConvertTest {
    @MockBean
    ChallengeSportService challengeSportService;

    @Autowired
    ChallengeSportConverter challengeSportConverter;

    @MockBean
    SportService sportService;
    @MockBean
    ChallengeService challengeService;
    @Autowired
    ChallengeConverter challengeConverter;

    List<ChallengeSport> challengeSportList ;
    List<ChallengeSportDTO> challengeSportDTOList ;
    ChallengeSportDTO challengeSportDTO;
    ChallengeSport challengeSport;


    @BeforeEach
    public void setup() throws AlreadyExistsException, NotFoundException {

        Sport sport = new Sport();
        sport.setId(2);
        when(sportService.get(2L)).thenReturn(sport);

        Challenge challenge = new Challenge();
        challenge.setId(1);
        Image image = new Image();
        image.setId(1L);
        challenge.setImage(image);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(challenge));

        challengeSport = new ChallengeSport();
        challengeSport.setId(3);
        challengeSport.setChallenge(challenge);
        challengeSport.setSport(sport);
        challengeSportList = new ArrayList<>();
        challengeSportList.add(challengeSport);

        challengeSportDTO = challengeSportConverter.convertEntityToDto(challengeSport);
        challengeSportDTOList = new ArrayList<>();
        challengeSportDTOList.add(challengeSportDTO);
    }

    @Test
    public void convertEntityToDtoTest(){
        ChallengeSportDTO a = challengeSportConverter.convertEntityToDto(challengeSport);
        assertEquals(1,a.getChallengeID());
        assertEquals(2,a.getSportID());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertEntityListToDtoListTEST(){
        List<ChallengeSportDTO> challengeSportDTOS = challengeSportConverter.convertEntityListToDtoList(challengeSportList);
        assertEquals(1,challengeSportDTOS.get(0).getChallengeID());
        assertEquals(2,challengeSportDTOS.get(0).getSportID());
        assertEquals(3,challengeSportDTOS.get(0).getId());
    }

    @Test
    public void convertDtoToEntityTEST() throws NotFoundException {
        ChallengeSport a = challengeSportConverter.convertDtoToEntity(challengeSportDTO);
        assertEquals(1,a.getChallenge().getId());
        assertEquals(2,a.getSport().getId());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertDtoListToEntityListTest() throws NotFoundException {
        List<ChallengeSport> challengeSports = challengeSportConverter.convertDtoListToEntityList(challengeSportDTOList);
        assertEquals(1,challengeSports.get(0).getChallenge().getId());
        assertEquals(2,challengeSports.get(0).getSport().getId());
        assertEquals(3,challengeSports.get(0).getId());
    }
}
