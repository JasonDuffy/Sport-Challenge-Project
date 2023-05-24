package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests of Activity Service
 *
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@SpringBootTest
public class ActivityServiceTest {
    @Autowired
    ActivityService activityService;
    @Autowired
    ActivityConverter activityConverter;
    @Autowired
    MemberConverter memberConverter;
    @Autowired
    BonusConverter bonusConverter;

    @MockBean
    ActivityRepository activityRepository;
    @MockBean
    ChallengeSportService challengeSportService;
    @MockBean
    MemberService memberService;
    @MockBean
    BonusService bonusService;
    @MockBean
    ChallengeService challengeService;

    @Autowired
    MemberConverter memberConverter;

    List<Activity> activityList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        activityList = new ArrayList<>();

        Challenge c1 = new Challenge();
        c1.setId(1L);

        ChallengeSport cs = new ChallengeSport();
        cs.setId(2L);
        cs.setChallenge(c1);
        Member m = new Member();
        m.setId(1L);

        for (long i = 0; i < 10; i++){
            Activity a = new Activity();
            a.setId(i);
            a.setChallengeSport(cs);
            a.setMember(m);
            activityList.add(a);
            when(activityRepository.findById(i)).thenReturn(Optional.of(a));
        }

        when(activityRepository.findAll()).thenReturn(activityList);
        when(challengeSportService.get(2L)).thenReturn(cs);
        when(memberService.get(1L)).thenReturn(memberConverter.convertEntityToDto(m));

        when(activityRepository.save(any(Activity.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given activity class
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<Activity> activities = activityConverter.convertDtoListToEntityList(activityService.getAll());

        for(Activity a : activities){
            boolean test = false;
            for (Activity a1 : activityList){
                if (a1.getId() == a.getId() && a1.getChallengeSport().getId() == a.getChallengeSport().getId()) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }

        assertEquals(activityList.size(), activities.size());

        verify(activityRepository).findAll();
    }

    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(Activity a : activityList){
            assertEquals(activityConverter.convertEntityToDto(a).getId(), activityService.get(a.getId()).getId());
            verify(activityRepository).findById(a.getId());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            activityService.get(15L);
        });
    }

    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestSuccess() throws NotFoundException {
        ChallengeSport cs = new ChallengeSport();
        cs.setId(1);
        when(challengeSportService.get(1L)).thenReturn(cs);

        Member m = new Member();
        m.setId(1L);
        when(memberService.get(1L)).thenReturn(memberConverter.convertEntityToDto(m));

        activityList.get(0).setChallengeSport(cs);
        activityList.get(0).setMember(m);

        ActivityDTO newActivity = activityService.add(activityConverter.convertEntityToDto(activityList.get(0)));

        assertEquals(newActivity.getId(), activityList.get(0).getId());
        assertEquals(newActivity.getChallengeSportID(), cs.getId());
        assertEquals(newActivity.getMemberID(), m.getId());

        verify(activityRepository).save(any(Activity.class));
        verify(challengeSportService).get(1L);
        verify(memberService).get(1L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
        when(challengeSportService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            activityService.add(activityConverter.convertEntityToDto(activityList.get(1)));
        });
    }

    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess() throws NotFoundException {
        ChallengeSport cs = new ChallengeSport();
        cs.setId(1);
        when(challengeSportService.get(1L)).thenReturn(cs);

        Member m = new Member();
        m.setId(1L);
        when(memberService.get(0L)).thenReturn(memberConverter.convertEntityToDto(m));

        activityList.get(1).setDistance(20.9f);
        activityList.get(1).setChallengeSport(cs);
        activityList.get(1).setMember(m);

        ActivityDTO newActivity = activityService.update(0L, activityConverter.convertEntityToDto(activityList.get(1)));

        assertEquals(newActivity.getId(), activityList.get(0).getId());
        assertEquals(newActivity.getDistance(), activityList.get(1).getDistance());
        assertEquals(newActivity.getChallengeSportID(), cs.getId());
        assertEquals(newActivity.getMemberID(), m.getId());

        verify(activityRepository).save(any(Activity.class));
        verify(challengeSportService).get(1L);
        verify(memberService).get(1L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {
        when(challengeSportService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            activityService.update(0L, activityConverter.convertEntityToDto(activityList.get(0)));
        });
    }

    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        activityService.delete(1L);
        verify(activityRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            activityService.delete(15L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        activityService.deleteAll();
        verify(activityRepository).deleteAll();
    }

    /**
     * Tests if getRawDistanceForActivities correctly sums up the distance
     * @throws InvalidActivitiesException Thrown when not all Activities are part of the same challenge
     */
    @Test
    public void getRawDistanceForActivitiesTest() throws InvalidActivitiesException {
        List<Activity> challenge1Acts = new ArrayList<>();

        for(Activity a : activityList){
            if (a.getChallengeSport().getChallenge().getId() == 1)
                challenge1Acts.add(a);
        }

        float ch1sum = 0.0f;

        for (Activity a : challenge1Acts)
            ch1sum += a.getDistance() * a.getChallengeSport().getFactor();

        assertEquals(ch1sum, activityService.getRawDistanceForActivities(challenge1Acts));
    }

    /**
     * Tests if empty array correctly results in 0 being returned
     * @throws InvalidActivitiesException Thrown when not all Activities are part of the same challenge
     */
    @Test
    public void getRawDistanceForActivitiesTestEmpty() throws InvalidActivitiesException {
        assertEquals(activityService.getRawDistanceForActivities(new ArrayList<>()), 0.0f);
    }

    /**
     * Tests if exception is being thrown when not all activities are part of the same challenge
     */
    @Test
    public void getRawDistanceForActivitiesTestException() {
        Challenge c2 = new Challenge();
        c2.setId(5L);
        ChallengeSport cs2 = new ChallengeSport();
        cs2.setChallenge(c2);
        activityList.get(0).setChallengeSport(cs2);
        assertThrows(InvalidActivitiesException.class, () -> {
            activityService.getRawDistanceForActivities(activityList);
        });
    }

    /**
     * Tests if getDistanceForActivities correctly sums up the distance
     * @throws InvalidActivitiesException Thrown when not all Activities are part of the same challenge
     */
    @Test
    public void getDistanceForActivitiesTest() throws InvalidActivitiesException, NotFoundException {
        BonusDTO b1 = new BonusDTO();
        b1.setId(1);
        b1.setStartDate(LocalDateTime.of(2023, 4, 10, 8, 0));
        b1.setEndDate(LocalDateTime.of(2023, 6, 4, 10, 0));
        b1.setFactor(2.0f);
        BonusDTO b2 = new BonusDTO();
        b2.setId(2);
        b2.setStartDate(LocalDateTime.of(2023, 4, 10, 8, 0));
        b2.setEndDate(LocalDateTime.of(2023, 6, 4, 10, 0));
        b2.setFactor(3.0f);

        List<BonusDTO> bonusList = new ArrayList<>();
        bonusList.add(b1); bonusList.add(b2);

        when(challengeService.getChallengeBonuses(1L)).thenReturn(bonusList);

        List<Activity> challenge1Acts = new ArrayList<>();

        for(Activity a : activityList){
            if (a.getChallengeSport().getChallenge().getId() == 1)
                challenge1Acts.add(a);
        }

        float distance = 0.0f;

        for (Activity a : challenge1Acts){
            float bonusfactor = 0.0f;

            for(BonusDTO b : bonusList){
                bonusfactor += b.getFactor();
            }

            if (bonusfactor == 0.0f)
                bonusfactor = 1.0f;

            distance += a.getDistance() * a.getChallengeSport().getFactor() * bonusfactor;
        }

        assertEquals(distance, activityService.getDistanceForActivities(challenge1Acts));
    }

    /**
     * Tests if empty array correctly results in 0 being returned
     * @throws InvalidActivitiesException Thrown when not all Activities are part of the same challenge
     */
    @Test
    public void getDistanceForActivitiesTestEmpty() throws InvalidActivitiesException, NotFoundException {
        assertEquals(activityService.getDistanceForActivities(new ArrayList<>()), 0.0f);
    }

    /**
     * Tests if exception is being thrown when not all activities are part of the same challenge
     */
    @Test
    public void getDistanceForActivitiesTestException() {
        Challenge c2 = new Challenge();
        c2.setId(5L);
        ChallengeSport cs2 = new ChallengeSport();
        cs2.setChallenge(c2);
        activityList.get(0).setChallengeSport(cs2);
        assertThrows(InvalidActivitiesException.class, () -> {
            activityService.getDistanceForActivities(activityList);
        });
    }
}
