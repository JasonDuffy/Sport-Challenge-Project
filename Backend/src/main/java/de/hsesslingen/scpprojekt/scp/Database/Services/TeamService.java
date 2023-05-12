package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ImageRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    @Autowired
    TeamRepository teamRepository;
    @Autowired
    ChallengeService challengeService;
    @Autowired
    ChallengeRepository challengeRepository;
    @Autowired
    ImageStorageService imageStorageService;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    TeamConverter teamConverter ;


    public List<TeamDTO> getAll() {
        List<Team> teamList = teamRepository.findAll();
        return  teamConverter.convertEntityListToDtoList(teamList);

    }


    public TeamDTO get(Long TeamID) throws NotFoundException {
        Optional<Team> team = teamRepository.findById(TeamID);
        if(team.isPresent()){
            return  teamConverter.convertEntityToDto(team.get());
        }throw new NotFoundException("Team with ID " +TeamID+" is not present in DB.");
    }


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



    public TeamDTO update(MultipartFile file, Long TeamID, TeamDTO team) throws NotFoundException {
        Optional<Team> teamData = teamRepository.findById(TeamID);
        Team convertedTeam = teamConverter.convertDtoToEntity(team);
        Optional<Challenge> challengeData = challengeRepository.findById(team.getChallengeID());
        if (teamData.isPresent()) {
            if (challengeData.isPresent()) {
                try {
                    Team updatedTeam = teamData.get();
                    Image teamImage = imageStorageService.store(file);

                    updatedTeam.setName(convertedTeam.getName());
                    updatedTeam.setImage(teamImage);
                    updatedTeam.setChallenge(challengeService.get(team.getChallengeID()));

                    Team savedTeam = teamRepository.save(updatedTeam);
                    return teamConverter.convertEntityToDto(savedTeam);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new NotFoundException("Team with ID " + TeamID + " is not present in DB.");
        }
        throw new NotFoundException("Challenge with ID " + team.getChallengeID() + " is not present in DB.");
    }


    public void delete(Long TeamID) throws NotFoundException {
        get(TeamID);
        teamRepository.deleteById(TeamID);
}


    public void deleteAll() {
        teamRepository.deleteAll();
    }
}
