package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.*;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

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
    ChallengeSportConverter challengeSportConverter;

    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    ChallengeSportService challengeSportService;

    @MockBean
    MemberService memberService;

    @Autowired
    ActivityService activityService;

    @Autowired
    ActivityConverter activityConverter;

    List<Activity> activityList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        activityList = new ArrayList<>();

        ChallengeSport cs = new ChallengeSport();
        cs.setId(2L);
        Member m = new Member();

        for (long i = 0; i < 10; i++){
            Activity a = new Activity();
            a.setId(i);
            a.setChallengeSport(cs);
            a.setMember(m);
            activityList.add(a);
            when(activityRepository.findById(i)).thenReturn(Optional.of(a));
        }

        when(activityRepository.findAll()).thenReturn(activityList);
        when(challengeSportConverter.convertDtoToEntity(challengeSportService.get(2L))).thenReturn(cs);
        when(memberService.get(1L)).thenReturn(memberConverter.convertEntityToDto(m));

        when(activityRepository.save(any(Activity.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given bonus class
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
        when(challengeSportConverter.convertDtoToEntity(challengeSportService.get(1L))).thenReturn(cs);

        Member m = new Member();
        when(memberService.get(0L)).thenReturn(m);

        activityList.get(0).setChallengeSport(cs);
        activityList.get(0).setMember(m);

        ActivityDTO newActivity = activityService.add(activityConverter.convertEntityToDto(activityList.get(0)));

        assertEquals(newActivity.getId(), activityList.get(0).getId());
        assertEquals(newActivity.getChallengeSportID(), cs.getId());
        assertEquals(newActivity.getMemberID(), m.getId());

        verify(activityRepository).save(any(Activity.class));
        verify(challengeSportService).get(1L);
        verify(memberService).get(0L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
        when(challengeSportService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            activityService.add(activityConverter.convertEntityToDto(activityList.get(0)));
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
        when(challengeSportConverter.convertDtoToEntity(challengeSportService.get(1L))).thenReturn(cs);

        Member m = new Member();
        when(memberService.get(0L)).thenReturn(m);

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
        verify(memberService).get(0L);
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
}
