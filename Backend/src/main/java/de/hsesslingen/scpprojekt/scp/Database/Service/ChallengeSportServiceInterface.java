package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;

import java.util.List;

public interface ChallengeSportServiceInterface {
    /**
     * Returns all ChallengeSports  in database
     * @return List of all ChallengeSports in DB
     */
    public List<ChallengeSport> getAll();

    /**
     * Returns ChallengeSport with given ID in DB
     * @param challengeSportID ID of desired ChallengeSport
     * @return ChallengeSport with given ID
     * @throws NotFoundException ChallengeSport can not be found
     */
    public ChallengeSport get(Long challengeSportID) throws NotFoundException;

    /**
     * Adds a given ChallengeSport to the DB
     * @param challengeSport ChallengeSport object to be added to DB
     * @return Added ChallengeSport object
     */
    public ChallengeSport add(ChallengeSport challengeSport);

    /**
     * Updates a ChallengeSport
     * @param challengeSportID ID of the ChallengeSport to be updated
     * @param challengeSport ChallengeSport object that overwrites the old ChallengeSport
     * @return Updated ChallengeSport object
     */
    public ChallengeSport update(Long challengeSportID, ChallengeSport challengeSport);

    /**
     * Deletes a specific ChallengeSport from the DB
     * @param challengeSportID ID of the ChallengeSport to be deleted
     */
    public void delete(Long challengeSportID);

    /**
     * Deletes all bonuses from the DB
     */
    public void deleteAll();
}
