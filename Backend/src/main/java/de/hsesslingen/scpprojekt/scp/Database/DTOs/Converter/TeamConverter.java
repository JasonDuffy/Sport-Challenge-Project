package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class TeamConverter {

    @Autowired
    ImageStorageService imageStorageService;
    @Autowired
    ChallengeService challengeService;
    @Autowired
    @Lazy
    ChallengeConverter challengeConverter;

    public TeamDTO convertEntityToDto(Team team) {
        TeamDTO teamDTO= new TeamDTO();
        teamDTO.setId(team.getId());
        teamDTO.setName(team.getName());
        try {
            teamDTO.setImageID(team.getImage().getId());
        } catch (NullPointerException e){
            teamDTO.setImageID(0);
        }
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
        try {
            team.setImage(imageStorageService.get(teamDTO.getImageID()));
        } catch (NullPointerException | NotFoundException e){
            team.setImage(null);
        }
        team.setChallenge(challengeConverter.convertDtoToEntity(challengeService.get(teamDTO.getChallengeID())));
        return team;
    }

    public List<Team> convertDtoListToEntityList(List<TeamDTO> teamDTOS) throws NotFoundException {
        List<Team> teamList = new ArrayList<>();

        for(TeamDTO newteamDTO: teamDTOS)
            teamList.add(convertDtoToEntity(newteamDTO));

        return teamList;
    }

}
