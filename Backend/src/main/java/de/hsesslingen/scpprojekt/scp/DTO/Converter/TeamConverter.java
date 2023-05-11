package de.hsesslingen.scpprojekt.scp.DTO.Converter;

import de.hsesslingen.scpprojekt.scp.DTO.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Service.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class TeamConverter {

    @Autowired
    ImageStorageService imageStorageService;
    @Autowired
    ChallengeService challengeService;

    public TeamDTO convertEntityToDto(Team team) {
        TeamDTO teamDTO= new TeamDTO();
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        teamDTO.setImageID(team.getImage().getId());
        teamDTO.setChallengeID(team.getChallenge().getId());
        return teamDTO;
    }

    public List<TeamDTO> convertEntityListToDtoList(List<Team> teamList){
        List<TeamDTO> teamDTOS = new ArrayList<>();

        for(Team team : teamList)
            teamDTOS.add(convertEntityToDto(team));

        return teamDTOS;
    }

    public Team convertDtoToEntity(TeamDTO teamDTO) throws NotFoundException {
        Team team = new Team();
        team.setId(teamDTO.getId());
        team.setName(teamDTO.getName());
        team.setImage(imageStorageService.get(teamDTO.getImageID()));
        team.setChallenge(challengeService.get(teamDTO.getChallengeID()));
        return team;
    }

    public List<Team> convertDtoListToEntityList(List<TeamDTO> teamDTOS) throws NotFoundException {
        List<Team> teamList = new ArrayList<>();

        for(TeamDTO newteamDTO: teamDTOS)
            teamList.add(convertDtoToEntity(newteamDTO));

        return teamList;
    }

}
