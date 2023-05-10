package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;

import java.util.List;

/**
 * Interface for all BonusService functions
 *
 * @author Jason Patrick Duffy
 */
public interface BonusServiceInterface {
    /**
     * Returns all bonuses in database
     * @return List of all Bonuses in DB
     */
    public List<Bonus> getAll();

    /**
     * Returns bonus with given ID in DB
     * @param bonusID ID of desired bonus
     * @return Bonus with given ID
     * @throws NotFoundException Bonus can not be found
     */
    public Bonus get(Long bonusID) throws NotFoundException;

    /**
     * Adds a given bonus to the DB
     * @param challengeSportID ID of the associated challenge sport for the bonus
     * @param bonus Bonus object to be added to DB
     * @return Added bonus object
     */
    public Bonus add(Long challengeSportID, Bonus bonus) throws  NotFoundException;

    /**
     * Updates a bonus
     * @param bonusID ID of the bonus to be updated
     * @param bonus Bonus object that overwrites the old bonus
     * @return Updated bonus object
     */
    public Bonus update(Long bonusID, Long challengeSportID, Bonus bonus) throws NotFoundException;

    /**
     * Deletes a specific bonus from the DB
     * @param bonusID ID of the bonus to be deleted
     */
    public void delete(Long bonusID) throws NotFoundException;

    /**
     * Deletes all bonuses from the DB
     */
    public void deleteAll();
}
