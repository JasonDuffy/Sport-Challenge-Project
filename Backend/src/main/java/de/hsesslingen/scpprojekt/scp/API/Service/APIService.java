package de.hsesslingen.scpprojekt.scp.API.Service;

import de.hsesslingen.scpprojekt.scp.Database.DTO.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTO.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTO.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTO.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Functions for the API.
 *
 * @author Jason Patrick Duffy
 */
@Component
public class APIService {
    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    BonusRepository bonusRepository;

    @Autowired
    ActivityConverter activityConverter;

    @Autowired
    BonusConverter bonusConverter;

    /**
     * Return all Activities for given Challenge ID
     * @param challengeID Challenge ID for returned activities
     * @return All activities concerning the given challenge ID
     */
    public List<ActivityDTO> getActivitiesForChallenge(Long challengeID){
        List<Activity> allActivities = activityRepository.findAll();
        List<Activity> challengeActivities = new ArrayList<>();

        for(Activity activity : allActivities){
            if(activity.getChallengeSport().getChallenge().getId() == challengeID)
                challengeActivities.add(activity);
        }

        return activityConverter.convertEntityListToDtoList(challengeActivities);
    }

    /**
     * Return all activities for given User ID
     * @param userID User ID for returned activities
     * @return All activities by the given user ID
     */
    public List<ActivityDTO> getActivitiesForUser(Long userID){
        List<Activity> allActivities = activityRepository.findAll();
        List<Activity> userActivities = new ArrayList<>();

        for(Activity activity : allActivities){
            if(activity.getMember().getId() == userID)
                userActivities.add(activity);
        }

        return activityConverter.convertEntityListToDtoList(userActivities);
    }

    /**
     * Returns all activities of a given user in a given challenge
     * @param challengeID Challenge ID of the requested activities
     * @param userID User ID of the requested activities
     * @return All Activities with the given User & Challenge IDs
     */
    public List<ActivityDTO> getActivitiesForUserInChallenge(Long challengeID, Long userID) throws NotFoundException {
        List<Activity> allUserActivities = activityConverter.convertDtoListToEntityList(getActivitiesForUser(userID));

        List<Activity> userChallengeActivities = new ArrayList<>();

        for(Activity activity : allUserActivities){
            if (activity.getChallengeSport().getChallenge().getId() == challengeID){
                userChallengeActivities.add(activity);
            }
        }

        return activityConverter.convertEntityListToDtoList(userChallengeActivities);
    }

    /**
     * Returns for activity distance without bonuses
     * @param activities List of activities for which the distance should be calculated. All have to be part of the same challenge.
     * @return Distance of activity without bonuses
     */
    public float getRawDistanceForActivities(List<Activity> activities) throws InvalidActivitiesException{
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
        float sum = 0.0f;

        if(!activities.isEmpty()){
            Challenge challenge = activities.get(0).getChallengeSport().getChallenge();
            List<Bonus> challengeBonuses = bonusConverter.convertDtoListToEntityList(getChallengeBonuses(challenge));

            for (Activity act : activities){
                if(act.getChallengeSport().getChallenge().getId() != challenge.getId()) //Checks if all activities are part of the same challenge
                    throw new InvalidActivitiesException("Activity by member " + act.getMember().getEmail() + " on " + act.getDate() + " in challenge "
                            + act.getChallengeSport().getChallenge().getName() + "is not part of Challenge " + challenge.getName());

                sum += act.getDistance() * act.getChallengeSport().getFactor() * getMultiplierFromBonuses(challengeBonuses, act.getDate());
            }
        }

        return sum;
    }

    /**
     * Returns all bonuses for a challenge
     * @param challenge The challenge for which the bonuses should be returned
     * @return The bonuses for the challenge
     */
    public List<BonusDTO> getChallengeBonuses(Challenge challenge){
        List<Bonus> bonuses = bonusRepository.findAll();
        List<Bonus> validBonuses = new ArrayList<>();

        for(Bonus bonus : bonuses){
            if(bonus.getChallengeSport().getChallenge().getId() == challenge.getId())
                validBonuses.add(bonus);
        }

        return bonusConverter.convertEntityListToDtoList(validBonuses);
    }

    /**
     * Return factor to apply to distance for given list of bonuses and given date
     * @param bonuses List of bonuses to be applied
     * @param checkDate Date to be checked
     * @return Factor for distance calculation
     */
    public float getMultiplierFromBonuses(List<Bonus> bonuses, LocalDateTime checkDate){
        float factor = 0.0f;

        for(Bonus bonus : bonuses){
            if(!bonus.getStartDate().isAfter(checkDate) && !bonus.getEndDate().isBefore(checkDate))
                factor += bonus.getFactor();
        }

        if ( factor == 0.0f )
            return 1.0f; //If no bonuses are applied, return factor as 1

        return factor;
    }
}
