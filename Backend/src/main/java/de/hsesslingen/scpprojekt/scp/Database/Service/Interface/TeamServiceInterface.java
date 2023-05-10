package de.hsesslingen.scpprojekt.scp.Database.Service.Interface;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * TeamServiceInterface
 *
 * @author Tom Nguyen Dinh
 */
public interface TeamServiceInterface {
    /**
     * Returns all teams in database
     * @return List of all teams in DB
     */
    public List<Team> getAll();

    /**
     * Returns team with given ID in DB
     * @param TeamID ID of desired Team
     * @return Team with given ID
     * @throws NotFoundException Team can not be found
     */
    public Team get(Long TeamID) throws NotFoundException;

    /**
     * Adds a given Team to the DB
     * @param Team Team object to be added to DB
     * @return Added Team object
     */
    public Team add(MultipartFile file,long ChallengeID, Team Team) throws  NotFoundException;


    /**
     * Updates a Team
     *
     * @param file image of the Team
     * @param TeamID ID of the Team to be updated
     * @param ChallengeID ID of the challenge which a team participates
     * @param team Team object that overwrites the old Team
     * @return Updated Team Object
     * @throws NotFoundException Challenge or Team cant be found
     */
    Team update(MultipartFile file, Long TeamID, long ChallengeID, Team team) throws NotFoundException;

    /**
     * Deletes a specific Team from the DB
     * @param TeamID ID of the bonus to be deleted
     */
    public void delete(Long TeamID) throws NotFoundException;

    /**
     * Deletes all Teams from the DB
     */
    public void deleteAll();
}

