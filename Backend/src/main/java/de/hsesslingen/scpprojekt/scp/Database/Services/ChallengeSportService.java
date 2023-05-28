package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service of the ChallengeSport entity
 *
 * @author Jason Patrick Duffy, Tom Nguyen Dinh
 */
@Service
public class ChallengeSportService {
    @Autowired
    ChallengeSportRepository challengeSportRepository;
    @Autowired
    @Lazy
    ChallengeSportConverter challengeSportConverter;
    @Autowired
    @Lazy
    ActivityService activityService;

    @Autowired
    @Lazy
    ActivityRepository activityRepository;


    /**
     * Returns all ChallengeSports  in database
     *
     * @return List of all ChallengeSports in DB
     */
    public List<ChallengeSportDTO> getAll() {
        List<ChallengeSport> challengeSportList = challengeSportRepository.findAll();
        return challengeSportConverter.convertEntityListToDtoList(challengeSportList);
    }

    public List<ChallengeSportDTO> getAllChallengeSportsOfChallenge(long ChallengeID)  {
        return challengeSportConverter.convertEntityListToDtoList(challengeSportRepository.findChallengeSportByChallenge_Id(ChallengeID));
    }

    /**
     * Returns ChallengeSport with given ID in DB
     *
     * @param challengeSportID ID of desired ChallengeSport
     * @return ChallengeSport with given ID
     * @throws NotFoundException ChallengeSport can not be found
     */
    public ChallengeSportDTO get(Long challengeSportID) throws NotFoundException {
        Optional<ChallengeSport> challengeSport = challengeSportRepository.findById(challengeSportID);
        if(challengeSport.isPresent())
            return challengeSportConverter.convertEntityToDto(challengeSport.get());
        throw new NotFoundException("ChallengeSport with ID " + challengeSportID + " is not present in DB.");
    }

    /**
     * Adds a given ChallengeSport to the DB
     *
     * @param challengeSport ChallengeSport object to be added to DB
     * @return Added ChallengeSport object
     */
    public ChallengeSportDTO add(ChallengeSportDTO challengeSport) throws NotFoundException {
        ChallengeSport challengeSportAdd = challengeSportConverter.convertDtoToEntity(challengeSport);
        ChallengeSport savedChallengeSport = challengeSportRepository.save(challengeSportAdd);
        return challengeSportConverter.convertEntityToDto(savedChallengeSport);
    }

    /**
     * Updates a ChallengeSport
     *
     * @param challengeSportID ID of the ChallengeSport to be updated
     * @param challengeSport   ChallengeSport object that overwrites the old ChallengeSport
     * @return Updated ChallengeSport object
     */
    public ChallengeSportDTO update(Long challengeSportID, ChallengeSportDTO challengeSport) throws NotFoundException, InvalidActivitiesException {
        Optional<ChallengeSport> optionalCS = challengeSportRepository.findById(challengeSportID);
        ChallengeSport convertedCS = challengeSportConverter.convertDtoToEntity(challengeSport);

        if(optionalCS.isPresent()){
            ChallengeSport CS = optionalCS.get();
            CS.setChallenge(convertedCS.getChallenge());
            CS.setSport(convertedCS.getSport());
            CS.setFactor(convertedCS.getFactor());

            List<Activity>  a =  activityRepository.findActivitiesByChallengeSport_Id(challengeSportID);
            activityService.calcTotalDistanceList(a);

            ChallengeSport savedCS = challengeSportRepository.save(CS);
            return challengeSportConverter.convertEntityToDto(savedCS);
        }

        throw new NotFoundException("Challenge-Sport with ID " + challengeSportID + " is not present in DB.");
    }

    /**
     * Deletes a specific ChallengeSport from the DB
     *
     * @param challengeSportID ID of the ChallengeSport to be deleted
     */
    public void delete(Long challengeSportID) throws NotFoundException {
        get(challengeSportID);
        challengeSportRepository.deleteById(challengeSportID);
    }

    /**
     *  Deletes all bonuses from the DB
     */
    public void deleteAll() {
        challengeSportRepository.deleteAll();
    }
}
