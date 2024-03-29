package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamMemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service of Team entity
 *
 * @auth Tom Nguyen Dinh
 */
@Service
public class TeamMemberService {
    @Autowired
    MemberService memberService;
    @Autowired
    TeamService teamService;
    @Autowired
    TeamMemberRepository teamMemberRepository;
    @Autowired
    @Lazy
    TeamMemberConverter teamMemberConverter;
    @Autowired
    @Lazy
    MemberConverter memberConverter;

    /**
     *  Return all TeamMembers
     * @return all TeamMembers
     */
    public List<TeamMemberDTO> getAll()  {
        List<TeamMember> teamMembers = teamMemberRepository.findAll();
        return teamMemberConverter.convertEntityListToDtoList(teamMembers);
    }

    /**
     *  Returns List TeamMember for a Challenge
     *
     * @param ChallengeID ID of Challenge
     * @return List of TeamMembers for the Challenge
     */
    public List<TeamMemberDTO> getAllTeamOfChallenge(long ChallengeID)  {
        List<TeamMember> teamMembers = teamMemberRepository.findAll();
        List<TeamMember> teamMemberList = new ArrayList<>();
        for (TeamMember teamMember : teamMembers){
            if(teamMember.getTeam().getChallenge().getId() == ChallengeID){
                teamMemberList.add(teamMember);
            }
        }
        return teamMemberConverter.convertEntityListToDtoList(teamMemberList);
    }

    /**
     * Returns activities with given ID in DB
     *
     * @param teamID ID of desired TeamMember
     * @return TeamMember with given ID
     * @throws NotFoundException TeamMember can not be found
     */
    public TeamMemberDTO get(Long teamID) throws NotFoundException {
        Optional<TeamMember> teamMember = teamMemberRepository.findById(teamID);
        if(teamMember.isPresent())
            return teamMemberConverter.convertEntityToDto(teamMember.get());
        throw new NotFoundException("TeamMember with ID " + teamID + " is not present in DB.");
    }

    /**
     * Returns the TeamMember with the given teamID and memberID
     *
     * @param memberID memberID that the TeamMember should contain
     * @param teamID teamID that the TeamMember should contain
     * @return TeamMember with given teamID and memberID
     * @throws NotFoundException TeamMember can not be found
     */
    public TeamMemberDTO getTeamMemberByTeamIdAndMemberId(long teamID, long memberID) throws NotFoundException {
        Optional<TeamMember> teamMember = teamMemberRepository.findTeamMemberByTeamIdAndMemberId(teamID, memberID);
        if(teamMember.isPresent())
            return teamMemberConverter.convertEntityToDto(teamMember.get());
        throw new NotFoundException("TeamMember with the teamID " + teamID + " and the memberID " + memberID + " is not present in DB.");
    }

    /**
     * Adds a given TeamMember to the DB
     *
     * @param teamMemberDTO  TeamMemberDTO object to be added to DB
     * @return Added TeamMember DTO object
     */
    public TeamMemberDTO add(TeamMemberDTO teamMemberDTO) throws NotFoundException {
        TeamMember a = teamMemberConverter.convertDtoToEntity(teamMemberDTO);
        TeamMember savedteamMember = teamMemberRepository.save(a);
        return teamMemberConverter.convertEntityToDto(savedteamMember);
    }

    /**
     * Updates an TeamMember
     *
     * @param teamMemberID       ID of the TeamMember to be updated
     * @param teamMember         TeamMemberDTO object that overwrites the old TeamMember
     * @return Updated TeamMember object
     */
    public TeamMemberDTO update(Long teamMemberID, TeamMemberDTO teamMember) throws NotFoundException {
        Optional<TeamMember> optionalTeamMember = teamMemberRepository.findById(teamMemberID);
        TeamMember convertedTeamMember = teamMemberConverter.convertDtoToEntity(teamMember);

        if(optionalTeamMember.isPresent()){
            TeamMember newTeamMember = optionalTeamMember.get();
            newTeamMember.setTeam(convertedTeamMember.getTeam());
            newTeamMember.setMember(convertedTeamMember.getMember());

            TeamMember savedTeamMember = teamMemberRepository.save(newTeamMember);
            return teamMemberConverter.convertEntityToDto(savedTeamMember);
        }

        throw new NotFoundException("Team-Member with ID " + teamMemberID + " is not present in DB.");
    }

    /**
     * Deletes a specific TeamMember from the DB
     *
     * @param teamMemberID ID of the TeamMember to be deleted
     */
    public void delete(long teamMemberID) throws NotFoundException {
        get(teamMemberID);
        teamMemberRepository.deleteById(teamMemberID);
    }

    /**
     * Deletes all Activities from the DB
     */
    public void deleteAll() {
        teamMemberRepository.deleteAll();
    }
}

