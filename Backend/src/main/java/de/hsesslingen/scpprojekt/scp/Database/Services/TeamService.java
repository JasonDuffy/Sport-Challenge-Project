package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.*;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service of Team entity
 *
 * @auth Tom Nguyen Dinh
 */
@Service
public class TeamService {

    @Autowired
    TeamRepository teamRepository;
    @Autowired
    ChallengeService challengeService;
    @Autowired
    ImageStorageService imageStorageService;
    @Autowired
    @Lazy
    TeamConverter teamConverter ;
    @Autowired
    @Lazy
    ChallengeConverter challengeConverter;
    @Autowired
    @Lazy
    MemberConverter memberConverter;

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ActivityConverter activityConverter;
    @Autowired
    TeamMemberRepository teamMemberRepository;


    /**
     * Returns all Teams in DB
     *
     * @return List of all Teams in DB
     */
    public List<TeamDTO> getAll() {
        List<Team> teamList = teamRepository.findAll();
        return  teamConverter.convertEntityListToDtoList(teamList);

    }

    /**
     * Returns Team with given ID in DB
     *
     * @param TeamID id of desired team
     * @return Team of ID
     * @throws NotFoundException Team can not be found
     */
    public TeamDTO get(Long TeamID) throws NotFoundException {
        Optional<Team> team = teamRepository.findById(TeamID);
        if(team.isPresent()){
            return teamConverter.convertEntityToDto(team.get());
        }
        throw new NotFoundException("Team with ID " +TeamID+" is not present in DB.");
    }


    /**
     * Add Team to DB
     *
     * @param file Image for the Team
     * @param teamDTO object to be added to DB
     * @return added Team
     * @throws NotFoundException
     */
    public TeamDTO add(MultipartFile file,TeamDTO teamDTO) throws NotFoundException {
                    try {
                        Image teamImage = imageStorageService.store(file);
                        Team team = teamConverter.convertDtoToEntity(teamDTO);
                        team.setImage(teamImage);
                        Team savedTeam = teamRepository.save(team);
                        return teamConverter.convertEntityToDto(savedTeam);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
    }

    /**
     * Updates a Team
     *
     * @param imageID ID of the new Image
     * @param TeamID ID of Team which should be updated
     * @param team team object with updated values
     * @return Updated Team
     * @throws NotFoundException not found Team or Challenge
     */
    public TeamDTO update(Long imageID, Long TeamID, TeamDTO team) throws NotFoundException {
        Optional<Team> teamData = teamRepository.findById(TeamID);
        Team convertedTeam = teamConverter.convertDtoToEntity(team);
        if (teamData.isPresent()) {
                Team updatedTeam = teamData.get();
                Image image = imageStorageService.get(imageID);
                updatedTeam.setName(convertedTeam.getName());
                updatedTeam.setChallenge(challengeConverter.convertDtoToEntity(challengeService.get(team.getChallengeID())));
                updatedTeam.setImage(image);

                Team savedTeam = teamRepository.save(updatedTeam);
                return teamConverter.convertEntityToDto(savedTeam);
        }throw new NotFoundException("Team with ID " + TeamID + " is not present in DB.");
    }

    /**
     * Deletes a Team
     *
     * @param TeamID ID of team to be deleted
     * @throws NotFoundException Not found Team
     */

    public void delete(Long TeamID) throws NotFoundException {
        get(TeamID);
        teamRepository.deleteById(TeamID);
}

    /**
     * Delete all Teams
     */
    public void deleteAll() {
        teamRepository.deleteAll();
    }

    /**
     *  Get Activity from a team of a challenge
     *
     * @param teamID ID of Team
     * @return List of Activities
     */
    public List<ActivityDTO> getTeamChallengeActivity(Long teamID) throws NotFoundException {
        TeamDTO team = get(teamID);
        List<ActivityDTO> a = activityConverter.convertEntityListToDtoList(activityRepository.findActivitiesByChallenge_ID(team.getChallengeID()));
        List <TeamMember> t = teamMemberRepository.findAllByTeamId(teamID);
        List<ActivityDTO> newA = new ArrayList<>();
        for (ActivityDTO as : a){
            for (TeamMember ts : t){
                if(as.getMemberID()==ts.getMember().getId()){
                    newA.add(as);
                }
            }
        }
        return newA;
    }

    public List<MemberDTO> getAllMembersByTeamID(long teamID){
        List<Member> members = teamRepository.findMembersByTeamID(teamID);
        return  memberConverter.convertEntityListToDtoList(members);
    }

}
