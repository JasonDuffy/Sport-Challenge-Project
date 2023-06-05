package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * Tests of TeamService
 *
 * @author Tom Nguyen Dinh
 */


@ActiveProfiles("test")
@SpringBootTest
public class TeamServiceTest {

    @MockBean
    TeamRepository teamRepository;
    @MockBean
    ChallengeService challengeService;
    @MockBean
    ImageStorageService imageStorageService;
    @MockBean
    Filler filler;

    @MockBean
    ActivityRepository activityRepository;
    @MockBean
    TeamMemberRepository teamMemberRepository;

    @Autowired
    TeamConverter teamConverter;
    @Autowired
    TeamService teamService;
    @Autowired
    ChallengeConverter challengeConverter;

    List<Team> teamList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        teamList = new ArrayList<>();

        Challenge challenge = new Challenge();
        challenge.setId(1L);

        Image image = mock(Image.class);
        image.setId(1L);

        for (long i = 0; i < 10; i++) {
            Team t1 = new Team();
            t1.setId(i);
            t1.setChallenge(challenge);
            t1.setImage(image);
            teamList.add(t1);
            when(teamRepository.findById(i)).thenReturn(Optional.of(t1));
        }
        when(teamRepository.findAll()).thenReturn(teamList);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(challenge));
        when(imageStorageService.get(1L)).thenReturn(image);

        when(teamRepository.save(any(Team.class))).then(AdditionalAnswers.returnsFirstArg());
    }
    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<Team> teams = teamConverter.convertDtoListToEntityList(teamService.getAll());

        for (Team t : teams) {
            boolean test = false;
            for (Team t1 : teamList) {
                if (t1.getId() == t.getId() && t1.getChallenge().getId() == t.getChallenge().getId()) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }
        assertEquals(teamList.size(), teams.size());
        verify(teamRepository).findAll();
    }

    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for (Team t : teamList) {
            assertEquals(teamConverter.convertEntityToDto(t).getId(), teamService.get(t.getId()).getId());
            verify(teamRepository).findById(t.getId());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail() {
        assertThrows(NotFoundException.class, () -> {
            teamService.get(15L);
        });
    }

    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown
     *
     */

    @Test
    public void addTestSuccess() throws NotFoundException {
        Challenge challenge = new Challenge();
        challenge.setId(1);
        Image image = new Image();
        image.setId(1);

        Team team = new Team();
        team.setImage(image);
        team.setChallenge(challenge);

        TeamDTO teamDTo = teamConverter.convertEntityToDto(team);
        teamDTo.setImageID(1L);
        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());
        TeamDTO newTeam = teamService.add(file, teamDTo);

        assertEquals(newTeam.getId(), teamList.get(0).getId());
        assertEquals(newTeam.getChallengeID(), challenge.getId());

        verify(teamRepository).save(any(Team.class));
        verify(challengeService).get(1L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
        when(challengeService.get(any(Long.class))).thenThrow(NotFoundException.class);
        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());

        assertThrows(NotFoundException.class, () -> {
            teamService.add(file, new TeamDTO(null, 0L, 0L));
        });
    }

    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess() throws NotFoundException {
        Challenge challenge = new Challenge();
        challenge.setId(1);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(challenge));

        teamList.get(1).setName("Team Dieter");

        TeamDTO newTeam = teamService.update(1L, 0L, teamConverter.convertEntityToDto(teamList.get(1)));

        assertEquals(newTeam.getId(), teamList.get(0).getId());
        assertEquals(newTeam.getName(), teamList.get(1).getName());
        assertEquals(newTeam.getChallengeID(), challenge.getId());

        verify(teamRepository).save(any(Team.class));
        verify(challengeService, times(2)).get(1L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {

        when(challengeService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            teamService.update(1L, 0L, teamConverter.convertEntityToDto(teamList.get(0)));
        });
    }

    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        teamService.delete(1L);
        verify(teamRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail() {
        assertThrows(NotFoundException.class, () -> {
            teamService.delete(15L);
        });
    }


    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest() {
        teamService.deleteAll();
        verify(teamRepository).deleteAll();
    }

    /**
     *  Test for getTeamChallengeActivity Success
     * @throws NotFoundException shouldn't be thrown
     */
    @Test
    public void getActivitiesFromTeamAndChallengeTestSuccess() throws NotFoundException {
        teamService.getTeamChallengeActivity(1L);
        verify(activityRepository).findActivitiesByChallenge_ID(1);
        verify(teamMemberRepository).findAllByTeamId(1);
    }
    /**  
     * Test if findMembersByTeamID works
     */
    @Test
    public void membersByTeamIDTest() {
        teamService.getAllMembersByTeamID(1);
        verify(teamRepository).findMembersByTeamID(1);
    }
}
