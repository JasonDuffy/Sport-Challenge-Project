package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public BonusDTO add(BonusDTO bonus) throws NotFoundException{
        Bonus b = bonusConverter.convertDtoToEntity(bonus);
        Bonus savedBonus = bonusRepository.save(b);
        return bonusConverter.convertEntityToDto(savedBonus);
    }

    /**
     * Updates a bonus
     *
     * @param bonusID ID of the bonus to be updated
     * @param bonus   Bonus object that overwrites the old bonus
     * @return Updated bonus object
     */
    public BonusDTO update(Long bonusID, BonusDTO bonus) throws NotFoundException{
        Optional<Bonus> optionalBonus = bonusRepository.findById(bonusID);
        Bonus convertedBonus = bonusConverter.convertDtoToEntity(bonus);

        if(optionalBonus.isPresent()){
            Bonus newBonus = optionalBonus.get();

            newBonus.setFactor(bonus.getFactor());
            newBonus.setName(bonus.getName());
            newBonus.setDescription(bonus.getDescription());
            newBonus.setEndDate(bonus.getEndDate());
            newBonus.setStartDate(bonus.getStartDate());
            newBonus.setId(bonus.getId());
            newBonus.setChallengeSport(challengeSportService.get(bonus.getChallengeSportID()));

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
