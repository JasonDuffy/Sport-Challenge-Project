package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamMemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
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
    @MockBean
    TeamMemberService teamMemberService;

    @Autowired
    TeamConverter teamConverter;
    @Autowired
    TeamService teamService;
    @Autowired
    ChallengeConverter challengeConverter;
    @Autowired
    ActivityConverter activityConverter;
    @Autowired
    TeamMemberConverter teamMemberConverter;

    List<Team> teamList;
    List<Activity> activityList;

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

        ChallengeSport challengeSport = new ChallengeSport();
        challengeSport.setId(1);
        challengeSport.setChallenge(challenge);

        Member member = new Member();
        member.setId(1);

        Activity activity = new Activity();
        activity.setId(1);
        activity.setChallengeSport(challengeSport);
        activity.setMember(member);

        Activity activity2 = new Activity();
        activity2.setId(2);
        activity2.setChallengeSport(challengeSport);
        activity2.setMember(member);

        activityList = new ArrayList<>();
        activityList.add(activity);
        activityList.add(activity2);
        when(activityRepository.findActivitiesByChallenge_ID(1)).thenReturn(activityList);

        Team team = new Team();
        team.setId(1);
        team.setChallenge(challenge);
        team.setImage(image);
        teamList.add(team);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

        TeamMember teamMember = new TeamMember(team,member);
        teamMember.setId(1);

        List<TeamMember> teamMembers = new ArrayList<>();
        teamMembers.add(teamMember);
        when(teamMemberService.get(1L)).thenReturn(teamMemberConverter.convertEntityToDto(teamMember));
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));
        when(teamMemberRepository.findAllByTeamId(1L)).thenReturn(teamMembers);


        for (long i = 2; i < 10; i++) {
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


        assertEquals(newTeam.getId(), 0);
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
     * Test is update works correctly TeamData non-existing
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess() throws NotFoundException {
        Challenge challenge = new Challenge();
        challenge.setId(1);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(challenge));

        teamList.get(1).setName("Team Dieter");

        TeamDTO newTeam = teamService.update(1L, 0L, teamConverter.convertEntityToDto(teamList.get(1)), new long[]{1});

        assertEquals(newTeam.getId(), teamList.get(1).getId());
        assertEquals(newTeam.getName(), teamList.get(1).getName());
        assertEquals(newTeam.getChallengeID(), challenge.getId());

        verify(teamRepository).save(any(Team.class));
        verify(challengeService, times(2)).get(1L);
        verify(teamMemberService).add(any(TeamMemberDTO.class));
    }

    /**
     * Test is update works correctly TeamData existing and imageID not null
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess2() throws NotFoundException {
        Challenge challenge = new Challenge();
        challenge.setId(1);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(challenge));

       Team team = new Team();
       team.setId(12);
       team.setChallenge(challenge);
       team.setName("BockWurst");
       when(teamRepository.findById(12L)).thenReturn(Optional.of(team));

       Member member = new Member();
       member.setId(3);

       List<Member> members = new ArrayList<>();
       members.add(member);

       when(teamRepository.findMembersByTeamID(12L)).thenReturn(members);

       TeamMemberDTO teamMember = new TeamMemberDTO(12,3);
       teamMember.setId(2);

       Image image = new Image();
       image.setId(1);

        TeamMemberDTO teamMember2 = new TeamMemberDTO(12,1);
        teamMember2.setId(2);
       when(teamMemberService.get(2L)).thenReturn(teamMember);
       when(teamMemberService.getTeamMemberByTeamIdAndMemberId(12L,3L)).thenReturn(teamMember);
       doNothing().when(teamMemberService).delete(any(Long.class));
       when(teamMemberService.add(any(TeamMemberDTO.class))).thenReturn(teamMember2);
       when(imageStorageService.get(1L)).thenReturn(image);


       TeamDTO newTeam = teamService.update(1L, 12L, teamConverter.convertEntityToDto(team), new long[]{1});
       assertEquals(newTeam.getChallengeID(),1);
       assertEquals(newTeam.getImageID(),1);
       assertEquals(newTeam.getName(),"BockWurst");

        verify(imageStorageService,times(2)).get(any(Long.class));
        verify(teamRepository).save(any(Team.class));
        verify(teamRepository).findMembersByTeamID(12L);
        verify(teamMemberService).getTeamMemberByTeamIdAndMemberId(12,3);
        verify(teamMemberService).delete(any(Long.class));
        verify(teamMemberService).add(any(TeamMemberDTO.class));
    }

    /**
     * Test is update works correctly when TeamData existing  and imageID null
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess3() throws NotFoundException {
        Challenge challenge = new Challenge();
        challenge.setId(1);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(challenge));

        Team team = new Team();
        team.setId(12);
        team.setChallenge(challenge);
        team.setName("BockWurst");
        when(teamRepository.findById(12L)).thenReturn(Optional.of(team));

        Member member = new Member();
        member.setId(3);

        List<Member> members = new ArrayList<>();
        members.add(member);

        when(teamRepository.findMembersByTeamID(12L)).thenReturn(members);

        TeamMemberDTO teamMember = new TeamMemberDTO(12,3);
        teamMember.setId(2);

        TeamMemberDTO teamMember2 = new TeamMemberDTO(12,1);
        teamMember2.setId(2);
        when(teamMemberService.get(2L)).thenReturn(teamMember);
        when(teamMemberService.getTeamMemberByTeamIdAndMemberId(12L,3L)).thenReturn(teamMember);
        doNothing().when(teamMemberService).delete(any(Long.class));
        when(teamMemberService.add(any(TeamMemberDTO.class))).thenReturn(teamMember2);
        when(imageStorageService.get(null)).thenThrow(NotFoundException.class);

        TeamDTO newTeam = teamService.update(null, 12L, teamConverter.convertEntityToDto(team), new long[]{1});
        assertEquals(newTeam.getChallengeID(),1);
        assertEquals(newTeam.getImageID(),0);
        assertEquals(newTeam.getName(),"BockWurst");

        verify(imageStorageService,times(1)).get(any(Long.class));
        verify(teamRepository).save(any(Team.class));
        verify(teamRepository).findMembersByTeamID(12L);
        verify(teamMemberService).getTeamMemberByTeamIdAndMemberId(12,3);
        verify(teamMemberService).delete(any(Long.class));
        verify(teamMemberService).add(any(TeamMemberDTO.class));
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {

        when(challengeService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            teamService.update(1L, 0L, teamConverter.convertEntityToDto(teamList.get(0)), new long[]{1});
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

    /**
     * Test if  countMembersOfTeam works
     */
    @Test
    public void getMemberCountForTeamTest() {
        teamService.getMemberCountForTeam(1);
        verify(teamRepository).countMembersOfTeam(1);
    }

    /**
     * Test if  getTeamChallengeActivity  works
     */
    @Test
    public void getTeamChallengeActivityTest() throws NotFoundException {
        List <ActivityDTO> activityDTOS = teamService.getTeamChallengeActivity(1L);
        assertEquals( activityDTOS.get(0).getId(), activityConverter.convertEntityToDto(activityList.get(0)).getId());
        assertEquals( activityDTOS.get(1).getId(), activityConverter.convertEntityToDto(activityList.get(1)).getId());
        verify(teamMemberRepository).findAllByTeamId(1);
    }


}
