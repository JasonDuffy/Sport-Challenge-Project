package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service of the ChallengeSport entity
 *
 * @author Jason Patrick Duffy
 */
@Service
public class ChallengeSportService {
    @Autowired
    ChallengeSportRepository challengeSportRepository;

    /**
     * Returns all ChallengeSports  in database
     *
     * @return List of all ChallengeSports in DB
     */
    public List<ChallengeSport> getAll() {
        return challengeSportRepository.findAll();
    }

    /**
     * Returns ChallengeSport with given ID in DB
     *
     * @param challengeSportID ID of desired ChallengeSport
     * @return ChallengeSport with given ID
     * @throws NotFoundException ChallengeSport can not be found
     */
    public ChallengeSport get(Long challengeSportID) throws NotFoundException {
        Optional<ChallengeSport> challengeSport = challengeSportRepository.findById(challengeSportID);
        if(challengeSport.isPresent())
            return challengeSport.get();
        throw new NotFoundException("ChallengeSport with ID " + challengeSportID + " is not present in DB.");
    }

    /**
     * TODO: Adds a given ChallengeSport to the DB
     *
     * @param challengeSport ChallengeSport object to be added to DB
     * @return Added ChallengeSport object
     */
    public ChallengeSport add(ChallengeSport challengeSport) {
        return null;
    }

    /**
     * TODO: Updates a ChallengeSport
     *
     * @param challengeSportID ID of the ChallengeSport to be updated
     * @param challengeSport   ChallengeSport object that overwrites the old ChallengeSport
     * @return Updated ChallengeSport object
     */
    public ChallengeSport update(Long challengeSportID, ChallengeSport challengeSport) {
        return null;
    }

    /**
     * TODO: Deletes a specific ChallengeSport from the DB
     *
     * @param challengeSportID ID of the ChallengeSport to be deleted
     */
    public void delete(Long challengeSportID) {

    }

    /**
     * TODO: Deletes all bonuses from the DB
     */
    public void deleteAll() {

    }
}
