package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service of the activity entity
 *
 * @author Jason Patrick Duffy
 */

@Service
public class ActivityService implements ActivityServiceInterface {
    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ChallengeSportService challengeSportService;

    @Autowired
    MemberService memberService;

    /**
     * Returns all activities in database
     *
     * @return List of all activities in DB
     */
    @Override
    public List<Activity> getAll() {
        return activityRepository.findAll();
    }

    /**
     * Returns activities with given ID in DB
     *
     * @param activityID ID of desired activity
     * @return Activity with given ID
     * @throws NotFoundException Activity can not be found
     */
    @Override
    public Activity get(Long activityID) throws NotFoundException {
        Optional<Activity> activity = activityRepository.findById(activityID);
        if(activity.isPresent())
            return activity.get();
        throw new NotFoundException("Activity with ID " + activityID + " is not present in DB.");
    }

    /**
     * Adds a given activity to the DB
     *
     * @param challengeSportID ID of the associated challenge sport for the activity
     * @param memberID         ID of the associated member for the activity
     * @param activity         Activity object to be added to DB
     * @return Added Activity object
     */
    @Override
    public Activity add(Long challengeSportID, Long memberID, Activity activity) throws NotFoundException {
        return activityRepository.save(new Activity(challengeSportService.get(challengeSportID), memberService.get(memberID), activity.getDistance(), activity.getDate()));
    }

    /**
     * Updates an activity
     *
     * @param activityID       ID of the activity to be updated
     * @param memberID         ID of the associated member
     * @param challengeSportID ID of the ChallengeSport object to be associated
     * @param activity         Activity object that overwrites the old activity
     * @return Updated Activity object
     */
    @Override
    public Activity update(Long activityID, Long memberID, Long challengeSportID, Activity activity) throws NotFoundException {
        Activity newActivity = get(activityID);

        newActivity.setMember(memberService.get(memberID));
        newActivity.setChallengeSport(challengeSportService.get(challengeSportID));
        newActivity.setDistance(activity.getDistance());
        newActivity.setDate(activity.getDate());

        return activityRepository.save(newActivity);
    }

    /**
     * Deletes a specific Activity from the DB
     *
     * @param activityID ID of the Activity to be deleted
     */
    @Override
    public void delete(Long activityID) throws NotFoundException {
        get(activityID);
        activityRepository.deleteById(activityID);
    }

    /**
     * Deletes all Activities from the DB
     */
    @Override
    public void deleteAll() {
        activityRepository.deleteAll();
    }
}
