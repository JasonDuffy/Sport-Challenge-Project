package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service of the Bonus entity
 *
 * @author Jason Patrick Duffy
 */

@Service
public class BonusService implements BonusServiceInterface{
    @Autowired
    BonusRepository bonusRepository;

    @Autowired
    ChallengeSportService challengeSportService;

    /**
     * Returns all bonuses in database
     *
     * @return List of all Bonuses in DB
     */
    @Override
    public List<Bonus> getAll() {
        return bonusRepository.findAll();
    }

    /**
     * Returns bonus with given ID in DB
     *
     * @param bonusID ID of desired bonus
     * @return Bonus with given ID
     * @throws NotFoundException Bonus can not be found
     */
    @Override
    public Bonus get(Long bonusID) throws NotFoundException {
        Optional<Bonus> bonus = bonusRepository.findById(bonusID);
        if(bonus.isPresent())
            return bonus.get();
        throw new NotFoundException("Bonus with ID " + bonusID + " is not present in DB.");
    }

    /**
     * Adds a given bonus to the DB
     *
     * @param challengeSportID ID of the associated challenge sport for the bonus
     * @param bonus            Bonus object to be added to DB
     * @return Added bonus object
     */
    @Override
    public Bonus add(Long challengeSportID, Bonus bonus) throws NotFoundException{
        return bonusRepository.save(new Bonus(challengeSportService.get(challengeSportID), bonus.getStartDate(), bonus.getEndDate(), bonus.getFactor(), bonus.getName(), bonus.getDescription()));
    }

    /**
     * Updates a bonus
     *
     * @param bonusID ID of the bonus to be updated
     * @param bonus   Bonus object that overwrites the old bonus
     * @return Updated bonus object
     */
    @Override
    public Bonus update(Long bonusID, Long challengeSportID, Bonus bonus) throws NotFoundException{
        Bonus newBonus = get(bonusID);

        newBonus.setFactor(bonus.getFactor());
        newBonus.setStartDate(bonus.getStartDate());
        newBonus.setEndDate(bonus.getEndDate());
        newBonus.setDescription(bonus.getDescription());
        newBonus.setName(bonus.getName());
        newBonus.setChallengeSport(challengeSportService.get(challengeSportID));

        return bonusRepository.save(newBonus);
    }

    /**
     * Deletes a specific bonus from the DB
     *
     * @param bonusID ID of the bonus to be deleted
     */
    @Override
    public void delete(Long bonusID) throws NotFoundException {
        get(bonusID);
        bonusRepository.deleteById(bonusID);
    }

    /**
     * Deletes all bonuses from the DB
     */
    @Override
    public void deleteAll() {
        bonusRepository.deleteAll();
    }
}
