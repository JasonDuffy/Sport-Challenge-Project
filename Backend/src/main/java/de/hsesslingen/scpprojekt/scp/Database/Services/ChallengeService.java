package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.*;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.*;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.*;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
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
    @Lazy
    ActivityConverter activityConverter;
    @Autowired
    @Lazy
    BonusConverter bonusConverter;
    @Autowired
    @Lazy
    TeamConverter teamConverter;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    @Lazy
    MemberConverter memberConverter;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    @Lazy
    ChallengeConverter challengeConverter;



    public List<ChallengeDTO> getAll() {
        List<Challenge> challengeListList = challengeRepository.findAll();
        return challengeConverter.convertEntityListToDtoList(challengeListList);
    }

    /**
     * Get Challenge with the ID
     *
     * @param ChallengeID ID of Challenge  to be searched
     * @return Challenge
     * @throws NotFoundException Not found Challenge
     */
    public Challenge get(Long ChallengeID) throws NotFoundException {
        Optional<Challenge> challenge = challengeRepository.findById(ChallengeID);
        if (challenge.isPresent()) {
            return challenge.get();
        }
        throw new NotFoundException("Challenge with ID " + ChallengeID + " is not present in DB.");
    }

    /**
     * Get ChallengeDTO with ID
     *
     * @param ChallengeID of the Challenge to be searched
     * @return Challenge
     * @throws NotFoundException Not found Challenge
     */

    public ChallengeDTO getDTO(Long ChallengeID) throws NotFoundException {
        Optional<Challenge> challenge = challengeRepository.findById(ChallengeID);
        if (challenge.isPresent()) {
            return challengeConverter.convertEntityToDto(challenge.get());
        }
        throw new NotFoundException("Challenge with ID " + ChallengeID + " is not present in DB.");
    }

    /**
     * Get ChallengeID's where the given MemberID is part of
     *
     * @param memberID memberID that should return all ChallengeID's the member is part of
     * @return ChallengeID's
     * @throws NotFoundException Not found any Challenge the Member is part of
     */
    public List<Long> getChallengeIDsByMemberID(Long memberID) throws NotFoundException {
        List<Long> challengeIDs = challengeRepository.findChallengeIDsByMemberID(memberID);
        if(!challengeIDs.isEmpty()){
            return challengeIDs;
        }else{
            throw new NotFoundException("The member wit the ID " + memberID + " is not part of a Challenge");
        }
    }


    public ChallengeDTO add(MultipartFile file, long sportId[], float sportFactor[], ChallengeDTO challenge) throws  IOException{
        Image image = imageStorageService.store(file);
        Challenge newchallenge = challengeConverter.convertDtoToEntity(challenge);
        newchallenge.setImage(image);
        Challenge savedChallenge = challengeRepository.save(newchallenge);
        return challengeConverter.convertEntityToDto(savedChallenge);
    }



    public ChallengeDTO update(long imageID, long ChallengeID, ChallengeDTO challengeDTO) throws NotFoundException {
        Optional<Challenge> challengeData = challengeRepository.findById(ChallengeID);
        Challenge convertedChallenge = challengeConverter.convertDtoToEntity(challengeDTO) ;
        if (challengeData.isPresent()){
                Image image = imageStorageService.get(imageID);
                Challenge updatedChallenge = challengeData.get();
                updatedChallenge.setName(convertedChallenge.getName());
                updatedChallenge.setImage(image);
                updatedChallenge.setDescription(convertedChallenge.getDescription());
                updatedChallenge.setStartDate(convertedChallenge.getStartDate());
                updatedChallenge.setEndDate(convertedChallenge.getEndDate());
                updatedChallenge.setTargetDistance(convertedChallenge.getTargetDistance());

                Challenge savedChallenge= challengeRepository.save(updatedChallenge);
                return challengeConverter.convertEntityToDto(savedChallenge);
        }throw  new NotFoundException("Challenge with ID " +ChallengeID+" is not present in DB.");
    }


    public void delete(long ChallengeID) throws NotFoundException {
        Optional<Challenge> challenge = challengeRepository.findById(ChallengeID);
        if (challenge.isPresent()){
            challengeRepository.deleteById(ChallengeID);
        }else throw  new NotFoundException("Challenge with ID " +ChallengeID+" is not present in DB.");
    }


    public void deleteAll() {
        challengeRepository.deleteAll();
    }

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
