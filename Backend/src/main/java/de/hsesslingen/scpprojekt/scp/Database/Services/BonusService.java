package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import de.hsesslingen.scpprojekt.scp.Mail.Services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service of the Bonus entity
 *
 * @author Jason Patrick Duffy
 */

@Service
public class BonusService {
    @Autowired
    @Lazy
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
    ChallengeService challengeService;

    @Autowired
    @Lazy
    ActivityRepository activityRepository;

    @Autowired
    @Lazy
    ActivityService activityService;

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
    public BonusDTO add(BonusDTO bonus) throws NotFoundException {
        Bonus b = bonusConverter.convertDtoToEntity(bonus);
        Bonus savedBonus = bonusRepository.save(b);

        sendBonusMail(savedBonus);

        return bonusConverter.convertEntityToDto(savedBonus);
    }

    /**
     * Sends an email to all members of a challenge, informing them of a new bonus
     *
     * @param bonus Bonus that they should be notified about
     */
    private void sendBonusMail(Bonus bonus){
        Map<String, Object> mailMap = new HashMap<>();
        mailMap.put("challengeName", bonus.getChallengeSport().getChallenge().getName());
        mailMap.put("bonusName", bonus.getName());
        mailMap.put("startTime", bonus.getStartDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        mailMap.put("endTime", bonus.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")));
        mailMap.put("factor", bonus.getFactor());
        mailMap.put("description", bonus.getDescription());
        mailMap.put("sport", bonus.getChallengeSport().getSport().getName());

        String subject = mailMap.get("challengeName") + " hat einen neuen Bonus!";

        try {
            emailService.sendBonusMail(challengeService.getChallengeMembers(bonus.getChallengeSport().getChallenge().getId()), subject, mailMap);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates a bonus
     *
     * @param bonusID ID of the bonus to be updated
     * @param bonus   Bonus object that overwrites the old bonus
     * @return Updated bonus object
     */
    public BonusDTO update(Long bonusID, BonusDTO bonus) throws NotFoundException, InvalidActivitiesException {
        Optional<Bonus> optionalBonus = bonusRepository.findById(bonusID);
        Bonus convertedBonus = bonusConverter.convertDtoToEntity(bonus);

        if(optionalBonus.isPresent()){
            Bonus newBonus = optionalBonus.get();

            newBonus.setFactor(convertedBonus.getFactor());
            newBonus.setName(convertedBonus.getName());
            newBonus.setDescription(convertedBonus.getDescription());
            newBonus.setEndDate(convertedBonus.getEndDate());
            newBonus.setStartDate(convertedBonus.getStartDate());
            newBonus.setChallengeSport(convertedBonus.getChallengeSport());

            List<Activity> a = activityRepository.findActivitiesByChallengeSport_Id(newBonus.getChallengeSport().getId());
            activityService.calcTotalDistanceList(a);

            Bonus savedBonus = bonusRepository.save(newBonus);
            return bonusConverter.convertEntityToDto(savedBonus);
        }

        throw new NotFoundException("Bonus with ID " + bonusID + " is not present in DB.");
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
}
