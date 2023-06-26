package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.*;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Service of Team entity
 *
 * @auth Tom Nguyen Dinh
 */
@Service
public class TeamService {

    @Autowired
    @Lazy
    TeamRepository teamRepository;
    @Autowired
    @Lazy
    ChallengeService challengeService;
    @Autowired
    @Lazy
    TeamMemberService teamMemberService;
    @Autowired
    @Lazy
    ImageStorageService imageStorageService;
    @Autowired
    @Lazy
    TeamConverter teamConverter;
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
     * @param teamID ID of Team which should be updated
     * @param team team object with updated values
     * @param memberIDs Arrays of members to add to the team
     * @return Updated Team
     * @throws NotFoundException not found Team or Challenge
     */
    public TeamDTO update(Long imageID, Long teamID, TeamDTO team, long[] memberIDs) throws NotFoundException {
        Optional<Team> teamData = teamRepository.findById(teamID);
        Team convertedTeam = teamConverter.convertDtoToEntity(team);
        if (teamData.isPresent()) {
                Team updatedTeam = teamData.get();

                Image image;
                try{
                     image = imageStorageService.get(imageID);
                } catch (NotFoundException e){
                    image = null;
                }

                updatedTeam.setName(convertedTeam.getName());
                updatedTeam.setChallenge(challengeConverter.convertDtoToEntity(challengeService.get(team.getChallengeID())));
                updatedTeam.setImage(image);

                Team savedTeam = teamRepository.save(updatedTeam);

                List<MemberDTO> memberList = getAllMembersByTeamID(teamID);
                List<Long> ids = new ArrayList<>();

                memberList.forEach((m) -> {
                    ids.add(m.getUserID());
                });

                for (Iterator<Long> id = ids.iterator(); id.hasNext(); ){
                    long currentNum = id.next();
                    if (!Arrays.stream(memberIDs).boxed().toList().contains(currentNum)){ // Delete all old members
                        teamMemberService.delete(teamMemberService.getTeamMemberByTeamIdAndMemberId(teamID, currentNum).getId());
                        id.remove();
                    }
                }

                for (Long id : memberIDs){
                    if (!ids.contains(id)){ // Add all new members
                        teamMemberService.add(new TeamMemberDTO(teamID, id));
                    }
                }

                return teamConverter.convertEntityToDto(savedTeam);
        } else {
            team.setImageID(imageID);
            Team t = teamConverter.convertDtoToEntity(team);
            Team savedTeam = teamRepository.save(t);

            for (long member : memberIDs){
                teamMemberService.add(new TeamMemberDTO(savedTeam.getId(), member));
            }

            return teamConverter.convertEntityToDto(savedTeam);
        }
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

    /**
     *
     * @param teamID
     * @return
     */
    public List<MemberDTO> getAllMembersByTeamID(long teamID){
        List<Member> members = teamRepository.findMembersByTeamID(teamID);
        return  memberConverter.convertEntityListToDtoList(members);
    }

    /**
     * Counts the members of the given team and returns it
     * @param teamID ID of the team whose member count is wanted
     * @return Member count of team
     */
    public int getMemberCountForTeam(long teamID){
        return teamRepository.countMembersOfTeam(teamID);
    }

}
