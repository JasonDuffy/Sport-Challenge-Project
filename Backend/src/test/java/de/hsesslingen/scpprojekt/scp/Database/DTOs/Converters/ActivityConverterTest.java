package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converters;


import de.hsesslingen.scpprojekt.scp.Database.DTOs.*;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Services.*;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test if the ActivityConverter is correct
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ActivityConverterTest {
    @MockBean
    ActivityService activityService;

    @Autowired
    ActivityConverter activityConverter;

    List<Activity> activityList ;
    List<ActivityDTO> activityDTOList ;
    ActivityDTO activityDTO;
    Activity activity;

    ChallengeSport challengeSport;

    Member member;

    @BeforeEach
    public void setup() throws AlreadyExistsException, NotFoundException {

        member = new Member();
        member.setId(1L);

        Sport sport = new Sport();
        sport.setId(1);

        Challenge challenge = new Challenge();
        challenge.setId(1);

        challengeSport = new ChallengeSport();
        challengeSport.setId(2);
        challengeSport.setChallenge(challenge);
        challengeSport.setSport(sport);

        activity = new Activity();
        activityList = new ArrayList<>();
        activity.setMember(member);
        activity.setId(3);
        activity.setChallengeSport(challengeSport);
        activityList.add(activity);

        activityDTO = activityConverter.convertEntityToDto(activity);
        activityDTOList = new ArrayList<>();
        activityDTOList.add(activityDTO);
    }
    @Test
    public void convertEntityToDtoTest(){
        ActivityDTO a = activityConverter.convertEntityToDto(activity);
        assertEquals(1,a.getMemberID());
        assertEquals(2,a.getChallengeSportID());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertEntityListToDtoListTEST(){
        List<ActivityDTO> activityDTOS = activityConverter.convertEntityListToDtoList(activityList);
        assertEquals(1,activityDTOS.get(0).getMemberID());
        assertEquals(2,activityDTOS.get(0).getChallengeSportID());
        assertEquals(3,activityDTOS.get(0).getId());
    }

    @Test
    public void convertDtoToEntityTEST() throws NotFoundException {
        Activity a = activityConverter.convertDtoToEntity(activityDTO);
        assertEquals(1,a.getMember().getId());
        assertEquals(2,a.getChallengeSport().getId());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertDtoListToEntityListTest() throws NotFoundException {
        List<Activity> activities = activityConverter.convertDtoListToEntityList(activityDTOList);
        assertEquals(1,activities.get(0).getMember().getId());
        assertEquals(2,activities.get(0).getChallengeSport().getId());
        assertEquals(3,activities.get(0).getId());
    }
}
