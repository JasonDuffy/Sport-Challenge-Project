package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converters;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
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
 * Test if the TeamConverter is correct
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class TeamConverterTest {
    @MockBean
    TeamService teamService;
    @Autowired
    TeamConverter teamConverter;

    List<Team> teamList;
    List<TeamDTO> teamDTOList ;
    TeamDTO teamDTO;
    Team team;


    @BeforeEach
    public void setup() throws AlreadyExistsException, NotFoundException {

        Image image = new Image();
        image.setId(2L);

        Challenge challenge = new Challenge();
        challenge.setId(1);

        team = new Team();
        team.setId(3);
        team.setChallenge(challenge);
        team.setImage(image);
        teamList = new ArrayList<>();
        teamList.add(team);

        teamDTO = teamConverter.convertEntityToDto(team);
        teamDTOList = new ArrayList<>();
        teamDTOList.add(teamDTO);
    }

    @Test
    public void convertEntityToDtoTest(){
        TeamDTO a = teamConverter.convertEntityToDto(team);
        assertEquals(1,a.getChallengeID());
        assertEquals(2,a.getImageID());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertEntityListToDtoListTEST(){
        List<TeamDTO> a = teamConverter.convertEntityListToDtoList(teamList);
        assertEquals(1,a.get(0).getChallengeID());
        assertEquals(2,a.get(0).getImageID());
        assertEquals(3,a.get(0).getId());
    }

    @Test
    public void convertDtoToEntityTEST() throws NotFoundException {
        Team a = teamConverter.convertDtoToEntity(teamDTO);
        assertEquals(1,a.getChallenge().getId());
        assertEquals(2,a.getImage().getId());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertDtoListToEntityListTest() throws NotFoundException {
        List<Team> a = teamConverter.convertDtoListToEntityList(teamDTOList);
        assertEquals(1,a.get(0).getChallenge().getId());
        assertEquals(2,a.get(0).getImage().getId());
        assertEquals(3,a.get(0).getId());
    }
}
