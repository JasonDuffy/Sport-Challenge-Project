package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
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
public class ActivityService {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ActivityConverter activityConverter;

    /**
     * Returns all activities in database
     *
     * @return List of all activities in DB
     */
    public List<ActivityDTO> getAll() {
        List<Activity> activities = activityRepository.findAll();
        return activityConverter.convertEntityListToDtoList(activities);
    }

    /**
     * Returns activities with given ID in DB
     *
     * @param activityID ID of desired activity
     * @return Activity with given ID
     * @throws NotFoundException Activity can not be found
     */
    public ActivityDTO get(Long activityID) throws NotFoundException {
        Optional<Activity> activity = activityRepository.findById(activityID);
        if(activity.isPresent())
            return activityConverter.convertEntityToDto(activity.get());
        throw new NotFoundException("Activity with ID " + activityID + " is not present in DB.");
    }

    /**
     * Adds a given activity to the DB
     *
     * @param activity         ActivityDTO object to be added to DB
     * @return Added Activity DTO object
     */
    public ActivityDTO add(ActivityDTO activity) throws NotFoundException {
        Activity a = activityConverter.convertDtoToEntity(activity);
        Activity savedActivity = activityRepository.save(a);
        return activityConverter.convertEntityToDto(savedActivity);
    }

    /**
     * Updates an activity
     *
     * @param activityID       ID of the activity to be updated
     * @param activity         ActivityDTO object that overwrites the old activity
     * @return Updated Activity object
     */
    public ActivityDTO update(Long activityID, ActivityDTO activity) throws NotFoundException {
        Optional<Activity> optionalActivity = activityRepository.findById(activityID);
        Activity convertedActivity = activityConverter.convertDtoToEntity(activity);

        if(optionalActivity.isPresent()){
            Activity newActivity = optionalActivity.get();
            newActivity.setMember(convertedActivity.getMember());
            newActivity.setChallengeSport(convertedActivity.getChallengeSport());
            newActivity.setDistance(convertedActivity.getDistance());
            newActivity.setDate(convertedActivity.getDate());

            Activity savedActivity = activityRepository.save(newActivity);
            return activityConverter.convertEntityToDto(savedActivity);
        }

        throw new NotFoundException("Activity with ID " + activityID + " is not present in DB.");
    }

    /**
     * Deletes a specific Activity from the DB
     *
     * @param activityID ID of the Activity to be deleted
     */
    public void delete(Long activityID) throws NotFoundException {
        get(activityID);
        activityRepository.deleteById(activityID);
    }

    /**
     * Deletes all Activities from the DB
     */
    public void deleteAll() {
        activityRepository.deleteAll();
    }
}
