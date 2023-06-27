package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportBonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportBonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSportBonus;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportBonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service of the ChallengeSportBonus
 *
 * @author Robin Hackh
 */
@Service
public class ChallengeSportBonusService {

    @Autowired
    ChallengeSportBonusRepository challengeSportBonusRepository;
    @Autowired
    @Lazy
    ChallengeSportBonusConverter challengeSportBonusConverter;
    @Autowired
    @Lazy
    ChallengeSportConverter challengeSportConverter;

    /**
     * Returns all ChallengeSportBonus in database
     *
     * @return List of all ChallengeSportsBonus in DB
     */
    public List<ChallengeSportBonusDTO> getAll() {
        List<ChallengeSportBonus> challengeSportList = challengeSportBonusRepository.findAll();
        return challengeSportBonusConverter.convertEntityToDtoList(challengeSportList);
    }

    /**
     * Returns ChallengeSportBonus with given ID in DB
     *
     * @param challengeSportBonusID ID of desired ChallengeSportBonus
     * @return ChallengeSportBonus with given ID
     * @throws NotFoundException ChallengeSportBonus can not be found
     */
    public ChallengeSportBonusDTO get(long challengeSportBonusID) throws NotFoundException {
        Optional<ChallengeSportBonus> challengeSportBonus = challengeSportBonusRepository.findById(challengeSportBonusID);
        if(challengeSportBonus.isPresent())
            return challengeSportBonusConverter.convertEntityToDto(challengeSportBonus.get());
        throw new NotFoundException("ChallengeSportBonus with ID " + challengeSportBonusID + " is not present in DB.");
    }

    /**
     * Adds a given ChallengeSportBonus to the DB
     *
     * @param challengeSportBonusDTO ChallengeSportBonus object to be added to DB
     * @return Added ChallengeSportBonus object
     */
    public ChallengeSportBonusDTO add(ChallengeSportBonusDTO challengeSportBonusDTO) throws NotFoundException {
        ChallengeSportBonus challengeSportBonusAdd = challengeSportBonusConverter.convertDtoToEntity(challengeSportBonusDTO);
        ChallengeSportBonus savedChallengeSportBonus = challengeSportBonusRepository.save(challengeSportBonusAdd);
        return challengeSportBonusConverter.convertEntityToDto(savedChallengeSportBonus);
    }

    /**
     * Updates a ChallengeSportBonus
     *
     * @param challengeSportBonusID ID of the ChallengeSportBonus to be updated
     * @param challengeSportBonusDTO ChallengeSportBonus object that overwrites the old ChallengeSportBonus
     * @return Updated ChallengeSportBonus object
     * @throws NotFoundException
     */
    public ChallengeSportBonusDTO update(long challengeSportBonusID, ChallengeSportBonusDTO challengeSportBonusDTO) throws NotFoundException {
        Optional<ChallengeSportBonus> foundChallengeSportBonus = challengeSportBonusRepository.findById(challengeSportBonusID);
        ChallengeSportBonus challengeSportBonusEntity = challengeSportBonusConverter.convertDtoToEntity(challengeSportBonusDTO);

        if(foundChallengeSportBonus.isPresent()){
            ChallengeSportBonus newChallengeSportBonus = foundChallengeSportBonus.get();
            newChallengeSportBonus.setChallengeSport(challengeSportBonusEntity.getChallengeSport());
            newChallengeSportBonus.setBonus(challengeSportBonusEntity.getBonus());

            ChallengeSportBonus challengeSportBonusSaved = challengeSportBonusRepository.save(newChallengeSportBonus);
            return challengeSportBonusConverter.convertEntityToDto(challengeSportBonusSaved);
        }

        throw new NotFoundException("ChallengeSportBonus with ID " + challengeSportBonusID + " is not present in DB.");
    }

    /**
     * Deletes a specific ChallengeSportBonus from the DB
     *
     * @param challengeSportBonusID ID of the ChallengeSportBonus to be deleted
     * @throws NotFoundException
     */
    public void delete(long challengeSportBonusID) throws NotFoundException {
        get(challengeSportBonusID);
        challengeSportBonusRepository.deleteById(challengeSportBonusID);
    }

    /**
     * Get all ChallengeSportBonus by BonusID
     *
     * @param bonusID ID of Bonus
     * @return List of ChallengeSportBonus
     */
    public List<ChallengeSportBonusDTO> findCSBByBonusID(long bonusID) {
        List<ChallengeSportBonus> csblist = challengeSportBonusRepository.findAllByBonusId(bonusID);
        return challengeSportBonusConverter.convertEntityToDtoList(csblist);
    }

    public List<ChallengeSportDTO> findCSbyBonusID(long bonusID) {
        List<ChallengeSport> csList = challengeSportBonusRepository.findChallengeSportForBonus(bonusID);
        return challengeSportConverter.convertEntityListToDtoList(csList);
    }
}
