package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.*;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.*;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for Challenge service functions
 *
 * @author Jason Patrick Duffy, Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeServiceTest {
    @Autowired
    ChallengeService challengeService;
    @Autowired
    ActivityConverter activityConverter;
    @Autowired
    BonusConverter bonusConverter;
    @Autowired
    TeamConverter teamConverter;
    @Autowired
    MemberConverter memberConverter;
    @Autowired
    ChallengeConverter challengeConverter;
    
    @MockBean
    ChallengeSportService challengeSportService;
    @MockBean
    ChallengeRepository challengeRepository;
    @MockBean
    ImageStorageService imageStorageService;
    @MockBean
    ActivityRepository activityRepository;
    @MockBean
    BonusRepository bonusRepository;
    @MockBean
    TeamRepository teamRepository;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    ChallengeSportRepository challengeSportRepository;
    @MockBean
    SportService sportService;

    @MockBean
    Filler filler; // Mocked to avoid exceptions

    List<Activity> activityList = new ArrayList<>();
    List<Challenge> challengeList = new ArrayList<>();
    List<ChallengeSport> challengeSportList = new ArrayList<>();
    List<Sport> sportList = new ArrayList<>();
    List<Member> memberList = new ArrayList<>();
    List<Bonus> bonusList = new ArrayList<>();
    List<Team> teamList = new ArrayList<>();
    List<TeamMember> teamMemberList = new ArrayList<>();

    @BeforeEach
    public void setup() throws NotFoundException {
        Member m1 = new Member();
        m1.setId(1L);
        m1.setEmail("test@example.com");
        Member m2 = new Member();
        m2.setId(2L);
        m2.setEmail("test2@example.com");
        memberList.add(m1); memberList.add(m2);

        Sport s1 = new Sport();
        s1.setId(1);
        s1.setFactor(2.0f);
        Sport s2 = new Sport();
        s2.setId(2);
        s2.setFactor(3.0f);
        sportList.add(s1); sportList.add(s2);
        when(sportService.get(1L)).thenReturn(s1);
        when(sportService.get(2L)).thenReturn(s2);

        Image image = new Image();
        image.setId(1);
        when(imageStorageService.get(1L)).thenReturn(image);

        Challenge ch1 = new Challenge();
        ch1.setId(1);
        ch1.setImage(image);
        Challenge ch2 = new Challenge();
        ch2.setId(2);
        ch2.setImage(image);
        Challenge ch3 = new Challenge();
        ch3.setId(3);
        ch3.setImage(image);
        challengeList.add(ch1); challengeList.add(ch2); challengeList.add(ch3);
        when(challengeRepository.findById(1L)).thenReturn(Optional.of(ch1));
        when(challengeRepository.findById(2L)).thenReturn(Optional.of(ch2));
        when(challengeRepository.findById(3L)).thenReturn(Optional.of(ch3));

        ChallengeSport cs1 = new ChallengeSport();
        cs1.setId(1);
        cs1.setChallenge(ch1);
        cs1.setSport(s1);
        cs1.setFactor(4.0f);
        when(challengeSportService.get(1L)).thenReturn(cs1);
        ChallengeSport cs2 = new ChallengeSport();
        cs2.setId(2);
        cs2.setChallenge(ch1);
        cs2.setSport(s2);
        cs2.setFactor(5.0f);
        when(challengeSportService.get(2L)).thenReturn(cs2);
        ChallengeSport cs3 = new ChallengeSport();
        cs3.setId(3);
        cs3.setChallenge(ch2);
        cs3.setSport(s1);
        cs3.setFactor(0.5f);
        when(challengeSportService.get(3L)).thenReturn(cs3);
        ChallengeSport cs4 = new ChallengeSport();
        cs4.setId(4);
        cs4.setChallenge(ch3);
        cs4.setSport(s2);
        cs4.setFactor(1.0f);
        when(challengeSportService.get(4L)).thenReturn(cs4);
        challengeSportList.add(cs1); challengeSportList.add(cs2); challengeSportList.add(cs3); challengeSportList.add(cs4);

        Bonus b1 = new Bonus();
        b1.setChallengeSport(cs1);
        b1.setId(1);
        b1.setStartDate(LocalDateTime.of(2023, 4, 10, 8, 0));
        b1.setEndDate(LocalDateTime.of(2023, 6, 4, 10, 0));
        b1.setFactor(2.0f);
        Bonus b2 = new Bonus();
        b2.setChallengeSport(cs1);
        b2.setId(2);
        b2.setStartDate(LocalDateTime.of(2023, 4, 10, 8, 0));
        b2.setEndDate(LocalDateTime.of(2023, 6, 4, 10, 0));
        b2.setFactor(3.0f);
        bonusList.add(b1); bonusList.add(b2);

        Team t1 = new Team();
        t1.setId(1L);
        t1.setChallenge(ch1);
        teamList.add(t1);
        when(teamRepository.findAll()).thenReturn(teamList);

        TeamMember tm1 = new TeamMember();
        tm1.setTeam(t1);
        tm1.setMember(m1);
        TeamMember tm2 = new TeamMember();
        tm2.setTeam(t1);
        tm2.setMember(m2);
        teamMemberList.add(tm1); teamMemberList.add(tm2);

        Activity a1 = new Activity();
        a1.setMember(m1);
        a1.setChallengeSport(cs1);
        a1.setId(1);
        a1.setDistance(10.0f);
        a1.setDate(LocalDateTime.of(2023, 5, 4, 10, 0));
        Activity a2 = new Activity();
        a2.setMember(m1);
        a2.setChallengeSport(cs1);
        a2.setId(2);
        a2.setDistance(5.0f);
        a2.setDate(LocalDateTime.of(2023, 7, 4, 10, 0));
        Activity a3 = new Activity();
        a3.setMember(m2);
        a3.setChallengeSport(cs1);
        a3.setId(3);
        a3.setDistance(15.0f);
        a3.setDate(LocalDateTime.of(2023, 7, 2, 10, 0));
        Activity a4 = new Activity();
        a4.setMember(m2);
        a4.setChallengeSport(cs3);
        a4.setId(4);
        a4.setDistance(25.0f);
        a4.setDate(LocalDateTime.of(2023, 1, 2, 10, 0));
        activityList.add(a1); activityList.add(a2); activityList.add(a3); activityList.add(a4);

        when(activityRepository.findAll()).thenReturn(activityList);
        when(bonusRepository.findAll()).thenReturn(bonusList);
        when(challengeRepository.findAll()).thenReturn(challengeList);

        when(memberRepository.findMembersByChallenge_ID(1L)).thenAnswer(a -> {
            List<Member> mList = new ArrayList<>();
            for(TeamMember tm : teamMemberList)
                if(tm.getTeam().getChallenge().getId() == 1L)
                    mList.add(tm.getMember());
            return mList;
        });
        when(bonusRepository.findBonusesByChallengeID(1L)).thenAnswer(a -> {
            List<Bonus> bList = new ArrayList<>();
            for(Bonus bon : bonusList)
                if (bon.getChallengeSport().getChallenge().getId() == 1L)
                    bList.add(bon);
            return bList;
        });
        when(activityRepository.findActivitiesByChallenge_ID(1L)).thenAnswer(a -> {
            List<Activity> aList = new ArrayList<>();
            for(Activity act : activityList)
                if (act.getChallengeSport().getChallenge().getId() == 1L)
                    aList.add(act);
            return aList;
        });
        when(activityRepository.findActivitiesByChallengeSport_Id(1L)).thenAnswer(a -> {
            List<Activity> aList = new ArrayList<>();
            for(Activity act : activityList)
                if (act.getChallengeSport().getId() == 1L)
                    aList.add(act);
            return aList;
        });
        when(activityRepository.findActivitiesByMember_Id(1L)).thenAnswer(a -> {
            List<Activity> aList = new ArrayList<>();
            for(Activity act : activityList)
                if (act.getMember().getId() == 1L)
                    aList.add(act);
            return aList;
        });
        when(challengeRepository.save(any(Challenge.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given challenge class
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<Challenge> challenges = challengeConverter.convertDtoListToEntityList(challengeService.getAll());
        for(Challenge c : challenges){
            boolean test = false;
            for (Challenge c1 : challengeList){
                if (c1.getId() == c.getId() && c1.getImage().getId() == c.getImage().getId()) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }
        assertEquals(challengeList.size(), challenges.size());
        verify(challengeRepository).findAll();
    }
    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(Challenge c : challengeList){
            assertEquals(challengeConverter.convertEntityToDto(c).getId(), challengeService.get(c.getId()).getId());
            verify(challengeRepository).findById(c.getId());
        }
    }
    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            challengeService.get(15L);
        });
    }

    /**
     * Tests if activities for challenges are returned correctly
     */
    @Test
    public void getActivitiesForChallengeTest() throws NotFoundException {
        List<ActivityDTO> acts = challengeService.getActivitiesForChallenge(1L);

        int counter = 0;

        for(ActivityDTO a : acts){
            counter++;
            assertEquals(challengeSportService.get(a.getChallengeSportID()).getChallenge().getId(), 1L);
        }

        int realCounter = 0;

        for(Activity a : activityList){
            if(a.getChallengeSport().getChallenge().getId() == 1L)
                realCounter++;
        }

        assertEquals(counter, realCounter);

        Mockito.verify(activityRepository).findActivitiesByChallenge_ID(1L);
    }

    /**
     * Tests if all bonuses are correctly returned
     */
    @Test
    public void getChallengeBonusesTest() throws NotFoundException {
        List<BonusDTO> bonuses = challengeService.getChallengeBonuses(challengeList.get(0).getId());

        int counter = 0;

        for(BonusDTO b : bonuses){
            assertEquals(1L, bonusConverter.convertDtoToEntity(b).getChallengeSport().getChallenge().getId());
            counter++;
        }

        int realCounter = 0;

        for(Bonus b : bonusList){
            if (b.getChallengeSport().getChallenge().getId() == 1)
                realCounter++;
        }

        assertEquals(counter, realCounter);

        Mockito.verify(bonusRepository).findBonusesByChallengeID(1L);
    }

    /**
     * Test if all teams are deleted
     */
    @Test
    public void deleteChallengeTeamsTest(){
        challengeService.deleteChallengeTeams(1L);

        verify(teamRepository).deleteAllByChallenge_Id(1L);
    }

    /**
     * Test if all members are returned correctly
     */
    @Test
    public void getChallengeMembersTest() {
        List<MemberDTO> members = challengeService.getChallengeMembers(1L);

        int counter = members.size();
        int realCounter = 0;

        for (TeamMember tm : teamMemberList)
            if (tm.getTeam().getChallenge().getId() == 1L)
                realCounter++;

        assertEquals(counter, realCounter);

        Mockito.verify(memberRepository).findMembersByChallenge_ID(1L);
    }
    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestSuccess() throws IOException {

        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());

        ChallengeDTO newC = challengeService.add(file,new long[]{1L},new float[]{10F},challengeConverter.convertEntityToDto(challengeList.get(0)));

        verify(challengeRepository).save(any(Challenge.class));

    }
    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     *
     */
    @Test
    public void updateTestSuccess() throws NotFoundException {

        challengeList.get(1).setName("name");

        ChallengeDTO newC = challengeService.update(1L,1L, challengeConverter.convertEntityToDto(challengeList.get(1)));

        assertEquals(newC.getId(), challengeList.get(0).getId());
        assertEquals(newC.getName(), challengeList.get(0).getName());

        verify(challengeRepository).save(any(Challenge.class));
    }


    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> {
            challengeService.update(1,20L, challengeConverter.convertEntityToDto(challengeList.get(0)));
        });
    }
    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        challengeService.delete(1L);
        verify(challengeRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            challengeService.delete(15L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        challengeService.deleteAll();
        verify(challengeRepository).deleteAll();
    }
}
