package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import de.hsesslingen.scpprojekt.scp.Database.Service.Interface.TeamServiceInterface;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService implements TeamServiceInterface {

    @Autowired
    TeamRepository teamRepository;
    @Autowired
    ChallengeRepository challengeRepository;
    @Autowired
    private  ImageStorageService imageStorageService;

    @Override
    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    @Override
    public Team get(Long TeamID) throws NotFoundException {
        Optional<Team> team = teamRepository.findById(TeamID);
        if(team.isPresent()){
            return team.get();
        }throw new NotFoundException("Team with ID " +TeamID+" is not present in DB.");
    }

    @Override
    public Team add(MultipartFile file, long ChallengeID, Team team) throws NotFoundException {
        Optional<Challenge> challenge1 = challengeRepository.findById(ChallengeID);
                if(challenge1.isPresent()) {
                    try {
                        Image teamImage = imageStorageService.store(file);
                        return teamRepository.save(new Team(team.getName(), teamImage, challenge1.get()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }throw  new NotFoundException("Challenge with ID " +ChallengeID+" is not present in DB.");
    }


    @Override
    public Team update(MultipartFile file, Long TeamID, long ChallengeID, Team team) throws NotFoundException {
        Optional<Team> teamData = teamRepository.findById(TeamID);
        Optional<Challenge> challengeData = challengeRepository.findById(ChallengeID);
        if (challengeData.isPresent()){
            if (teamData.isPresent()){
                try {
                    Image teamImage = imageStorageService.store(file);
                    Team updatedTeam = teamData.get();
                    updatedTeam.setName(team.getName());
                    updatedTeam.setImage(teamImage);
                    updatedTeam.setChallenge(challengeData.get());

                    return teamRepository.save(updatedTeam);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                    }
            }throw  new NotFoundException("Team with ID " +TeamID+" is not present in DB.");
        }throw  new NotFoundException("Challenge with ID " +ChallengeID+" is not present in DB.");

    }

    @Override
    public void delete(Long TeamID) throws NotFoundException {
        Optional<Team> teamData = teamRepository.findById(TeamID);
        if (teamData.isPresent()){
            teamRepository.deleteById(TeamID);
        }throw  new NotFoundException("Team with ID " +TeamID+" is not present in DB.");
}

    @Override
    public void deleteAll() {
        teamRepository.deleteAll();
    }
}
