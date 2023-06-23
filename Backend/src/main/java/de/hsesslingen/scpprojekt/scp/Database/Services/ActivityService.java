package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.ActivityDateException;
import de.hsesslingen.scpprojekt.scp.Exceptions.InactiveChallengeException;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service of the activity entity
 *
 * @author Jason Patrick Duffy, Tom Nguyen Dinh
 */

@Service
public class ActivityService {
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    @Lazy
    ActivityConverter activityConverter;

    @Autowired
    @Lazy
    BonusConverter bonusConverter;

    @Autowired
    BonusService bonusService;

    @Autowired
    ChallengeService challengeService;

    private static final String DATE_FORMATTER= "dd.MM.yyyy, HH:mm";

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
    public ActivityDTO add(ActivityDTO activity) throws NotFoundException, InactiveChallengeException, ActivityDateException {
        Activity a = activityConverter.convertDtoToEntity(activity);
        Challenge challenge = a.getChallengeSport().getChallenge();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime active = a.getDate();
        if (challenge.getEndDate().isAfter(now) && now.isAfter(challenge.getStartDate())) {
            if (challenge.getEndDate().isAfter(active) && active.isAfter(challenge.getStartDate())) {
                a.setTotalDistance(calcTotalDistance(a));
                Activity savedActivity = activityRepository.save(a);
                return activityConverter.convertEntityToDto(savedActivity);
            }
            else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
                String activityDate = a.getDate().format(formatter);
                String startDate = challenge.getStartDate().format(formatter);
                String endDate = challenge.getEndDate().format(formatter);
                throw new ActivityDateException
                        ("The date of "+activityDate+" the activity is not in the challenge period of "+startDate+" - "+endDate+" .");
            }
        }else
            throw new InactiveChallengeException("This activity is denied, because the challenge "+challenge.getName() +" is no longer active");
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
            newActivity.setTotalDistance(calcTotalDistance(newActivity));
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

    /**
     * Returns for activity distance without bonuses
     * @param activities List of activities for which the distance should be calculated. All have to be part of the same challenge.
     * @return Distance of activity without bonuses
     */
    public float getRawDistanceForActivities(List<Activity> activities) throws InvalidActivitiesException {
        float sum = 0.0f;

        if(!activities.isEmpty()){
            Challenge challenge = activities.get(0).getChallengeSport().getChallenge();

            for (Activity act : activities){
                if(act.getChallengeSport().getChallenge().getId() != challenge.getId()) //Checks if all activities are part of the same challenge
                    throw new InvalidActivitiesException("Activity by member " + act.getMember().getEmail() + " on " + act.getDate() + " in challenge "
                            + act.getChallengeSport().getChallenge().getName() + "is not part of Challenge " + challenge.getName());

                sum += act.getDistance() * act.getChallengeSport().getFactor();
            }
        }
        return sum;
    }

    /**
     * Returns the activity distance WITH bonuses
     * @param activities List of activities for which the distance should be calculated. All have to be part of the same challenge.
     * @return Distance of activities with bonuses
     */
    public float getDistanceForActivities(List<Activity> activities) throws InvalidActivitiesException, NotFoundException {
        Float sum = 0.0f;

        if(!activities.isEmpty()){
            Challenge challenge = activities.get(0).getChallengeSport().getChallenge();

            for (Activity act : activities){
                if(act.getChallengeSport().getChallenge().getId() != challenge.getId()) //Checks if all activities are part of the same challenge
                    throw new InvalidActivitiesException("Activity by member " + act.getMember().getEmail() + " on " + act.getDate() + " in challenge "
                            + act.getChallengeSport().getChallenge().getName() + "is not part of Challenge " + challenge.getName());

                sum += act.getTotalDistance();
            }
        }

        return sum;
    }

    /**
     *  Avg Distance for activities
     * @param memberCount Count of Members in an activity
     * @param activities List of activities
     * @return Avg Distance
     * @throws InvalidActivitiesException Invalid Activity
     * @throws NotFoundException Challenge not Found
     */
    public float getAVGDistanceForActivities(int memberCount, List<Activity> activities) throws InvalidActivitiesException, NotFoundException {
        Float sum = 0.0f;

        if(!activities.isEmpty()){
            Challenge challenge = activities.get(0).getChallengeSport().getChallenge();

            for (Activity act : activities){
                if(act.getChallengeSport().getChallenge().getId() != challenge.getId()) //Checks if all activities are part of the same challenge
                    throw new InvalidActivitiesException("Activity by member " + act.getMember().getEmail() + " on " + act.getDate() + " in challenge "
                            + act.getChallengeSport().getChallenge().getName() + "is not part of Challenge " + challenge.getName());
                sum += act.getTotalDistance();
            }
        }

        return sum/(float)memberCount;
    }


    /**
     * function updates the DB with the totalDistance
     *
     * @throws NotFoundException Challenge not found
     */
    public void totalDistanceAll() throws NotFoundException {
        List<Activity> a = activityRepository.findAll();
        for(Activity as : a){
            as.setTotalDistance(calcTotalDistance(as));
            activityRepository.save(as);
        }
    }

    /**
     * Calculates the totalDistance for an activity
     * @param activity which gets a totalDistance
     * @return float totalDistance
     */
    public float calcTotalDistance(Activity activity) {
        float total = 0f;
        long challengeId = activity.getChallengeSport().getChallenge().getId();
        long sportId = activity.getChallengeSport().getSport().getId();
        LocalDateTime time = activity.getDate();
        total = activity.getDistance() * activity.getChallengeSport().getFactor() * bonusService.getMultiplierFromBonusesForChallengeAndSportAndSpecificTime(challengeId, sportId, time);
        return total;
    }

    /**
     * Calculates the totalDistance for an activity
     * @param activities List of activities to be updated for totalDistance
     * @throws NotFoundException Challenge not found
     */
    public void calcTotalDistanceList(List<Activity> activities) throws InvalidActivitiesException, NotFoundException {
        if(!activities.isEmpty()){
            Challenge challenge = activities.get(0).getChallengeSport().getChallenge();

            for (Activity act : activities){
                if(act.getChallengeSport().getChallenge().getId() != challenge.getId()) //Checks if all activities are part of the same challenge
                    throw new InvalidActivitiesException("Activity by member " + act.getMember().getEmail() + " on " + act.getDate() + " in challenge "
                            + act.getChallengeSport().getChallenge().getName() + "is not part of Challenge " + challenge.getName());
                act.setTotalDistance(calcTotalDistance(act));
                activityRepository.save(act);
            }
        }
    }

}
