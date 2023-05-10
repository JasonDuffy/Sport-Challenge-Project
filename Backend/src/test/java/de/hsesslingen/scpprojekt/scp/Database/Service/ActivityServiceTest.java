package de.hsesslingen.scpprojekt.scp.Database.Service;

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
    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    ChallengeSportService challengeSportService;

    @MockBean
    MemberService memberService;

    @Autowired
    ActivityService activityService;

    List<Activity> activityList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup(){
        activityList = new ArrayList<>();

        for (long i = 0; i < 10; i++){
            Activity a = new Activity();
            a.setId(i);
            activityList.add(a);
            when(activityRepository.findById(i)).thenReturn(Optional.of(a));
        }

        when(activityRepository.findAll()).thenReturn(activityList);

        when(activityRepository.save(any(Activity.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given bonus class
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest(){
        List<Activity> activities = activityService.getAll();

        for(Activity a : activities)
            assertTrue(activityList.contains(a));

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
            assertEquals(a, activityService.get(a.getId()));
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
        when(memberService.get(0L)).thenReturn(m);

        Activity newActivity = activityService.add(1L, 0L, activityList.get(0));

        assertEquals(newActivity.getId(), activityList.get(0).getId());
        assertEquals(newActivity.getChallengeSport(), cs);
        assertEquals(newActivity.getMember(), m);

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
            activityService.add(1L, 0L, activityList.get(0));
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
        when(memberService.get(0L)).thenReturn(m);

        activityList.get(1).setDistance(20.9f);

        Activity newActivity = activityService.update(0L, 0L, 1L, activityList.get(1));

        assertEquals(newActivity.getId(), activityList.get(0).getId());
        assertEquals(newActivity.getDistance(), activityList.get(1).getDistance());
        assertEquals(newActivity.getChallengeSport(), cs);
        assertEquals(newActivity.getMember(), m);

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
            activityService.update(0L, 0L, 1L, activityList.get(0));
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
