package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.*;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service of the Challenge
 *
 * @author Tom Nguyen Dinh, Jason Patrick Duffy
 */
@Service
public class ChallengeService {

    @Autowired
    ChallengeRepository challengeRepository;
    @Autowired
    ImageStorageService imageStorageService;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    BonusRepository bonusRepository;
    @Autowired
    ActivityConverter activityConverter;
    @Autowired
    BonusConverter bonusConverter;
    @Autowired
    @Lazy
    TeamConverter teamConverter;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    MemberConverter memberConverter;
    @Autowired
    MemberRepository memberRepository;



/*
    public List<ChallengeDTO> getAll() {
        List<Challenge> challengeListList = challengeRepository.findAll();
        return  challengeConverter.convertEntityListToDtoList(challengeListList);

    }
*/

    /**
     * Get Challenge with the ID
     *
     * @param ChallengeID ID of Challenge  to be searched
     * @return Challenge
     * @throws NotFoundException Not found Challenge
     */
    public Challenge get(Long ChallengeID) throws NotFoundException {
        Optional<Challenge> challenge = challengeRepository.findById(ChallengeID);
        if(challenge.isPresent()){
            return  challenge.get();
        }throw new NotFoundException("Challenge with ID " +ChallengeID+" is not present in DB.");
    }


    /**public Challenge add(MultipartFile file, Challenge challenge) throws NotFoundException {
        try {
            Image image = imageStorageService.store(file);
            Challenge newchallenge = new Challenge();
            newchallenge.setImage(image);
            newchallenge.setName(challenge.getName());
            newchallenge.setDescription(challenge.getDescription());
            newchallenge.setStartDate(challenge.getStartDate());
            newchallenge.setEndDate(challenge.getEndDate());
            newchallenge.setTargetDistance(challenge.getTargetDistance());
            Challenge savedChallenge = challengeRepository.save(newchallenge);
            return savedChallenge;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public ChallengeDTO update(MultipartFile file, long ChallengeID, ChallengeDTO challengeDTO) throws NotFoundException {
        Optional<Challenge> challengeData = challengeRepository.findById(ChallengeID);
        Challenge convertedChallenge = challengeConverter.convertDtoToEntity(challengeDTO) ;
        if (challengeData.isPresent()){
            try {
                Image image = imageStorageService.store(file);
                Challenge updatedChallenge = challengeData.get();
                updatedChallenge.setName(convertedChallenge.getName());
                updatedChallenge.setImage(image);
                updatedChallenge.setDescription(convertedChallenge.getDescription());
                updatedChallenge.setStartDate(convertedChallenge.getStartDate());
                updatedChallenge.setEndDate(convertedChallenge.getEndDate());
                updatedChallenge.setTargetDistance(convertedChallenge.getTargetDistance());

                Challenge savedChallenge= challengeRepository.save(updatedChallenge);
                return challengeConverter.convertEntityToDto(savedChallenge);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }throw  new NotFoundException("Challenge with ID " +ChallengeID+" is not present in DB.");
    }


    public void delete(long ChallengeID) throws NotFoundException {
        Optional<Challenge> challenge = challengeRepository.findById(ChallengeID);
        if (challenge.isPresent()){
            challengeRepository.deleteById(ChallengeID);
        }throw  new NotFoundException("Challenge with ID " +ChallengeID+" is not present in DB.");
    }


    public void deleteAll() {
        challengeRepository.deleteAll();
    }
     **/

    /**
     * Return all Activities for given Challenge ID
     * @param challengeID Challenge ID for returned activities
     * @return All activities concerning the given challenge ID
     */
    public List<ActivityDTO> getActivitiesForChallenge(Long challengeID){
        return activityConverter.convertEntityListToDtoList(activityRepository.findActivitiesByChallenge_ID(challengeID));
    }

    /**
     * Returns all bonuses for a challenge
     * @param challengeID The challengeID for which the bonuses should be returned
     * @return The bonuses for the challenge
     */
    public List<BonusDTO> getChallengeBonuses(long challengeID){
        return bonusConverter.convertEntityListToDtoList(bonusRepository.findBonusesByChallengeID(challengeID));
    }

    /**
     * Returns all teams for a challenge
     * @param challengeID The challengeID for which the teams should be returned
     * @return The teams for the challenge
     */
    public List<TeamDTO> getChallengeTeams(long challengeID){
        return teamConverter.convertEntityListToDtoList(teamRepository.findTeamsByChallenge_Id(challengeID));
    }

    /**
     * Deletes all teams from a challenge
     * @param challengeID The challengeID for which the teams should be deleted
     */
    public void deleteChallengeTeams(long challengeID){
        teamRepository.deleteAllByChallenge_Id(challengeID);
    }

    /**
     * Returns all members for a challenge
     * @param challengeID The challengeID for which the teams should be deleted
     * @return The members of a challenge
     */
    public List<MemberDTO> getChallengeMembers(long challengeID){
        return memberConverter.convertEntityListToDtoList(memberRepository.findMembersByChallenge_ID(challengeID));
    }
}
