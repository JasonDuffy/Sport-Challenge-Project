package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service of Sports
 *
 * @auth Tom Nguyen Dinh
 */
@Service
public class SportService {
    @Autowired
    SportRepository sportRepository;
    

    /**
     * Returns all activities in database
     *
     * @return List of all activities in DB
     */
    public List<Sport> getAll() {
       return sportRepository.findAll();
    }

    /**
     * Returns activities with given ID in DB
     *
     * @param SportID ID of desired Sport
     * @return Sport  with given ID
     * @throws NotFoundException Sport  can not be found
     */
    public Sport get(Long SportID) throws NotFoundException {
        Optional<Sport> sport  = sportRepository.findById(SportID);
        if(sport.isPresent())
            return sport.get();
        throw new NotFoundException("Sport  with ID " + SportID + " is not present in DB.");
    }

    /**
     * Adds a given Sport  to the DB
     *
     * @param sport Sport object to be added to DB
     * @return Added Sport object
     */
    public Sport add(Sport sport){
        return sportRepository.save(new Sport(sport.getName(),sport.getFactor()));
    }

    /**
     * Updates a Sport
     *
     * @param SportID   ID of the Sport  to be updated
     * @param sport   Sport  object that overwrites the old Sport
     * @return Updated Sport  object
     */
    public Sport update(Long SportID, Sport sport) throws NotFoundException {
        Optional<Sport > optionalSport  = sportRepository.findById(SportID);
        if(optionalSport.isPresent()){
            Sport newSport  = optionalSport.get();
            newSport.setName(sport.getName());
            newSport.setFactor(sport.getFactor());

            return sportRepository.save(newSport);
        }
        throw new NotFoundException("Sport  with ID " + SportID + " is not present in DB.");
    }

    /**
     * Deletes a specific Sport  from the DB
     *
     * @param SportID ID of the Sport  to be deleted
     */
    public void delete(Long SportID) throws NotFoundException {
        get(SportID);
        sportRepository.deleteById(SportID);
    }

    /**
     * Deletes all Activities from the DB
     */
    public void deleteAll() {
        sportRepository.deleteAll();
    }
}

