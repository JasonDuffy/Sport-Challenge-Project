package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts Activity Entity to DTO and vice-versa
 *
 * @author Jason Patrick Duffy
 */
@Component
public class ActivityConverter {
    @Autowired
    ChallengeSportService challengeSportService;
    @Autowired
    MemberService memberService;
    @Autowired
    ChallengeSportConverter challengeSportConverter;
    @Autowired
    MemberConverter memberConverter;

    public ActivityDTO convertEntityToDto(Activity activity) {
        ActivityDTO activityDTO = new ActivityDTO();
        activityDTO.setId(activity.getId());
        activityDTO.setDate(activity.getDate());
        activityDTO.setDistance(activity.getDistance());
        activityDTO.setChallengeSportID(activity.getChallengeSport().getId());
        activityDTO.setMemberID(activity.getMember().getId());
        return activityDTO;
    }

    public List<ActivityDTO> convertEntityListToDtoList(List<Activity> activityList){
        List<ActivityDTO> activityDTOS = new ArrayList<>();

        for(Activity activity : activityList)
            activityDTOS.add(convertEntityToDto(activity));

        return activityDTOS;
    }

    public Activity convertDtoToEntity(ActivityDTO activityDTO) throws NotFoundException {
        Activity activity = new Activity();
        activity.setId(activityDTO.getId());
        activity.setDistance(activityDTO.getDistance());
        activity.setDate(activityDTO.getDate());
        activity.setChallengeSport(challengeSportConverter.convertDtoToEntity(challengeSportService.get(activityDTO.getChallengeSportID())));
        activity.setMember(memberConverter.convertDtoToEntity(memberService.get(activityDTO.getMemberID())));
        return activity;
    }

    public List<Activity> convertDtoListToEntityList(List<ActivityDTO> activityDTOS) throws NotFoundException {
        List<Activity> activities = new ArrayList<>();

        for(ActivityDTO activityDTO : activityDTOS)
            activities.add(convertDtoToEntity(activityDTO));

        return activities;
    }
}
