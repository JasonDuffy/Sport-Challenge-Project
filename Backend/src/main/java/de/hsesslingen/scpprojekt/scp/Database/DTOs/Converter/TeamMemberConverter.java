package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts TeamMember Entity  to DTO and vice versa
 *
 * @auth Tom Nguyen Dinh
 */
@Component
public class TeamMemberConverter {
    @Autowired
    TeamService teamService;
    @Autowired
    MemberService memberService;

    /**
     * Converts Entity TeamMember to a DTO
     *
     * @param teamMember as Entity
     * @return TeamMember as DTo
     */

    public TeamMemberDTO convertEntityToDto(TeamMember teamMember) {
        TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
        teamMemberDTO.setId(teamMember.getId());
        teamMemberDTO.setTeamID(teamMember.getTeam().getId());
        teamMemberDTO.setMemberID(teamMember.getMember().getId());
        return teamMemberDTO;
    }

    /**
     * Converts Entity List to DTO List
     *
     * @param teamMemberList List of TeamMembers
     * @return List of TeamMember as DTO
     * @throws NotFoundException Not Found TeamMember
     */
    public List<TeamMemberDTO > convertEntityListToDtoList(List<TeamMember> teamMemberList)  {
        List<TeamMemberDTO> TeamMemberDTOList = new ArrayList<>();
        for(TeamMember teamMember : teamMemberList)
            TeamMemberDTOList.add(convertEntityToDto(teamMember));

        return TeamMemberDTOList;
    }

    /**
     * Converts DTO TeamMember to Entity
     *
     * @param teamMemberDTO Dto of TeamMember
     * @return TeamMember Entity
     * @throws NotFoundException Not Found TeamMember
     */
    public TeamMember convertDtoToEntity(TeamMemberDTO  teamMemberDTO ) throws NotFoundException {
        TeamMember teamMember = new TeamMember();
        teamMember.setId(teamMemberDTO.getId());
        teamMember.setTeam(teamService.get(teamMemberDTO.getTeamID()));
        teamMember.setMember(memberService.get((teamMemberDTO.getMemberID())));
        return teamMember;
    }

    /**
     * Converts Dto List of TeamMember to Entity List
     *
     * @param teamMemberDTOList DTo list of TeamMember
     * @return Entity List of TeamMember
     * @throws NotFoundException Not found TeamMember
     */
    public List<TeamMember> convertDtoListToEntityList(List<TeamMemberDTO > teamMemberDTOList) throws NotFoundException {
        List<TeamMember> teamMemberList = new ArrayList<>();
        for(TeamMemberDTO  teamMemberDTO : teamMemberDTOList)
            teamMemberList.add(convertDtoToEntity(teamMemberDTO));

        return teamMemberList;
    }
}

