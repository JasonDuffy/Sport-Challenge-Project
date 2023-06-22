package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportBonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import de.hsesslingen.scpprojekt.scp.Mail.Services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

/**
 * Service of the Bonus entity
 *
 * @author Jason Patrick Duffy
 */

@Service
public class BonusService {
    @Autowired
    BonusRepository bonusRepository;

    @Autowired
    ChallengeSportService challengeSportService;

    @Autowired
    @Lazy
    BonusConverter bonusConverter;
    @Autowired
    @Lazy
    ChallengeSportConverter challengeSportConverter;

    @Autowired
    EmailService emailService;

    @Autowired
    @Lazy
    ActivityRepository activityRepository;

    @Autowired
    @Lazy
    ActivityService activityService;

    @Autowired
    @Lazy
    ChallengeSportBonusService challengeSportBonusService;


    /**
     * Returns all bonuses in database
     *
     * @return List of all Bonuses in DB
     */
    public List<BonusDTO> getAll() {
        List<Bonus> bonusList = bonusRepository.findAll();
        return bonusConverter.convertEntityListToDtoList(bonusList);
    }

    /**
     * Returns bonus with given ID in DB
     *
     * @param bonusID ID of desired bonus
     * @return Bonus with given ID
     * @throws NotFoundException Bonus can not be found
     */
    public BonusDTO get(Long bonusID) throws NotFoundException {
        Optional<Bonus> bonus = bonusRepository.findById(bonusID);
        if(bonus.isPresent())
            return bonusConverter.convertEntityToDto(bonus.get());
        throw new NotFoundException("Bonus with ID " + bonusID + " is not present in DB.");
    }

    /**
     * Adds a given bonus to the DB
     *
     * @param bonus            Bonus object to be added to DB
     * @return Added bonus object
     */
    public BonusDTO add(BonusDTO bonus, long[] challengeSportID) throws NotFoundException, InvalidActivitiesException {
        Bonus b = bonusConverter.convertDtoToEntity(bonus);
        Bonus savedBonus = bonusRepository.save(b);
        for (int i = 0; i < challengeSportID.length; i++) {
            ChallengeSportBonusDTO csb = new ChallengeSportBonusDTO();
            csb.setBonusID(b.getId());
            csb.setChallengeSportID(challengeSportID[i]);
            challengeSportBonusService.add(csb);
        }
        List<ChallengeSportBonusDTO> csbList = challengeSportBonusService.findCSBByBonusID(b.getId());
        for (ChallengeSportBonusDTO cb : csbList){
            List<Activity> a = activityRepository.findActivitiesByChallengeSport_Id(cb.getChallengeSportID());
            activityService.calcTotalDistanceList(a);
        }
        
       try {
            emailService.sendBonusMail(savedBonus);
        } catch (MessagingException e) {
            System.out.println("Bonus mail for bonus " + bonus.getName() + " could not be sent!");
        }

        return bonusConverter.convertEntityToDto(savedBonus);
    }

    /**
     * Updates or adds a bonus
     *
     * @param bonusID ID of the bonus to be updated
     * @param challengeSportID Array of ChallengeSport IDs for which the bonus should be applied
     * @param bonus Bonus object that overwrites the old bonus
     * @return Updated or added bonus object
     */
    public BonusDTO update(Long bonusID, BonusDTO bonus, long[] challengeSportID) throws NotFoundException, InvalidActivitiesException {
        Optional<Bonus> optionalBonus = bonusRepository.findById(bonusID);
        Bonus convertedBonus = bonusConverter.convertDtoToEntity(bonus);

        if(optionalBonus.isPresent()){
            Bonus newBonus = optionalBonus.get();

            newBonus.setFactor(convertedBonus.getFactor());
            newBonus.setName(convertedBonus.getName());
            newBonus.setDescription(convertedBonus.getDescription());
            newBonus.setEndDate(convertedBonus.getEndDate());
            newBonus.setStartDate(convertedBonus.getStartDate());

            List<ChallengeSportBonusDTO> csbList = challengeSportBonusService.findCSBByBonusID(newBonus.getId());
            List<Long> existingCSs = new ArrayList<>();
            for (ChallengeSportBonusDTO cb : csbList) { // Delete all old challenge sport bonuses
                if (!(Arrays.stream(challengeSportID).boxed().toList()).contains(cb.getChallengeSportID())){
                    challengeSportBonusService.delete(cb.getId());
                }
                else{
                    existingCSs.add(cb.getChallengeSportID());
                }
            }

            for (long id : challengeSportID){
                if (!existingCSs.contains(id)){
                    challengeSportBonusService.add(new ChallengeSportBonusDTO(id, newBonus.getId()));
                }
                List<Activity> a = activityRepository.findActivitiesByChallengeSport_Id(id);
                activityService.calcTotalDistanceList(a);
            }

            Bonus savedBonus = bonusRepository.save(newBonus);
            return bonusConverter.convertEntityToDto(savedBonus);
        } else { // Add if bonus not found
            return add(bonus, challengeSportID);
        }
    }

    /**
     * Deletes a specific bonus from the DB
     *
     * @param bonusID ID of the bonus to be deleted
     */
    public void delete(Long bonusID) throws NotFoundException {
        get(bonusID);
        bonusRepository.deleteById(bonusID);
    }

    /**
     * Deletes all bonuses from the DB
     */
    public void deleteAll() {
        bonusRepository.deleteAll();
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

    /**
     * Returns all bonuses for the given challenge
     * @param challengeID ID of the challenge for which the bonuses should be returned
     * @param type "past" for past bonuses, "current" for current bonuses, "future" for future bonuses and anything else for all
     * @return List of BonusDTO objects corresponding to the given options
     */
    public List<BonusDTO> getChallengeBonuses(long challengeID, String type){
        switch(type){
            case "past":
                return bonusConverter.convertEntityListToDtoList(bonusRepository.findPastBonusesByChallengeID(challengeID));
            case "current":
                return bonusConverter.convertEntityListToDtoList(bonusRepository.findCurrentBonusesByChallengeID(challengeID));
            case "future":
                return bonusConverter.convertEntityListToDtoList(bonusRepository.findFutureBonusesByChallengeID(challengeID));
            default:
                return bonusConverter.convertEntityListToDtoList(bonusRepository.findBonusesByChallengeID(challengeID));
        }
    }

    /**
     * Returns multiplier for given challenges and sport at this time
     * @param challengeID The challengeID for which multiplier should be returned
     * @param sportID The sportsID for which the multiplier should be returned
     * @return The calculated multiplier at this moment
     */
    public float getCurrentMultiplierFromBonusesForChallengeAndSport(long challengeID, long sportID){
        List<Bonus> bonuses = bonusRepository.findCurrentBonusesByChallengeIDAndSportID(challengeID, sportID);
        return getMultiplierFromBonuses(bonuses, LocalDateTime.now());
    }


    /**
     * Returns multiplier for given challenges and sport at the given date
     * @param challengeID The challengeID for which multiplier should be returned
     * @param sportID The sportsID for which the multiplier should be returned
     * @param time The time for which the factor should be calculated
     * @return The calculated multiplier at the given time
     */
    public float getMultiplierFromBonusesForChallengeAndSportAndSpecificTime(long challengeID, long sportID, LocalDateTime time){
        List<Bonus> bonuses = bonusRepository.findBonusesByChallengeIDAndSportIDAtSpecificTime(challengeID, sportID, time);
        return getMultiplierFromBonuses(bonuses, time);
    }

    /**
     * Returns the sports associated to a given bonus
     * @param bonusID ID of bonus for which the sports should be retrieved
     * @return List of Sports for bonus
     */
    public List<Sport> getSportsForBonus(long bonusID){
        return bonusRepository.findSportsForBonus(bonusID);
    }
}
