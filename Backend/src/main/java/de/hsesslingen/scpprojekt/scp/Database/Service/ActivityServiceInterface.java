package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.DTO.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;

import java.util.List;

/**
 * Interface for all ActivityService functions
 *
 * @author Jason Patrick Duffy
 */
public interface ActivityServiceInterface {
    /**
     * Returns all activities in database
     * @return List of all activities in DB
     */
    public List<ActivityDTO> getAll();

    /**
     * Returns activities with given ID in DB
     * @param activityID ID of desired activity
     * @return Activity with given ID
     * @throws NotFoundException Activity can not be found
     */
    public ActivityDTO get(Long activityID) throws NotFoundException;

    /**
     * Adds a given activity to the DB
     * @param activity Activity object to be added to DB
     * @return Added Activity DTO object
     */
    public ActivityDTO add(ActivityDTO activity) throws NotFoundException;

    /**
     * Updates an activity
     * @param activityID ID of the activity to be updated
     * @param activity Activity object that overwrites the old activity
     * @return Updated Activity object
     */
    public ActivityDTO update(Long activityID, ActivityDTO activity) throws NotFoundException;

    /**
     * Deletes a specific Activity from the DB
     * @param activityID ID of the Activity to be deleted
     */
    public void delete(Long activityID) throws NotFoundException;

    /**
     * Deletes all Activities from the DB
     */
    public void deleteAll();
}
