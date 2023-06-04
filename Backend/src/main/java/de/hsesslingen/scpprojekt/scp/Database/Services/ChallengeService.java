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
import de.hsesslingen.scpprojekt.scp.Mail.Services.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
    @Autowired
    EmailService emailService;
    @Autowired
    ChallengeSportService challengeSportService;
    @Autowired
    SportService sportService;

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
    public ChallengeDTO get(Long ChallengeID) throws NotFoundException {
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


    /**
     *  add Challenge
     * @param file Image data
     * @param sportId IDs for the sports
     * @param sportFactor Factors for the sports in this challenge
     * @param challenge data of Challenge
     * @return A new challenge Entity added to the db
     * @throws IOException not an image
     * @throws NotFoundException Sport not found
     */
    public ChallengeDTO add(MultipartFile file, long sportId[], float sportFactor[], ChallengeDTO challenge) throws IOException, NotFoundException {
        Image image = imageStorageService.store(file);
        Challenge newchallenge = challengeConverter.convertDtoToEntity(challenge);
        newchallenge.setImage(image);
        Challenge savedChallenge = challengeRepository.save(newchallenge);

        for (int i = 0; i < sportId.length; i++) {
            ChallengeSportDTO cs = new ChallengeSportDTO();
            cs.setChallengeID(newchallenge.getId());
            cs.setFactor(sportFactor[i]);
            cs.setSportID(sportId[i]);
            challengeSportService.add(cs);
        }

        try {
            emailService.sendChallengeMail(savedChallenge);
        } catch (MessagingException | NotFoundException e) {
            System.out.println("Could not send email for challenge " + savedChallenge.getName());
        }

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
     * @param challengeID The challengeID for which the members should be found
     * @return The members of a challenge
     */
    public List<MemberDTO> getChallengeMembers(long challengeID){
        return memberConverter.convertEntityListToDtoList(memberRepository.findMembersByChallenge_ID(challengeID));
    }

    /**
     * Returns all emails of members of a challenge that are opted into receiving emails
     * @param challengeID The challengeID for which the emails should be found
     * @return All emails of members of a challenge that are opted into receiving emails
     */
    public List<String> getChallengeMembersEmails(long challengeID){
        return memberRepository.findMembersEmailByChallengeID(challengeID);
    }

    public List<ChallengeDTO> getCurrentChallengeMemberID(long memberID) throws NotFoundException {
        List<Challenge> challengeList = challengeRepository.findChallengesByMemberIDAndDate(memberID, LocalDateTime.now());
        if (!challengeList.isEmpty()) {
            return challengeConverter.convertEntityListToDtoList(challengeList);
        } else {
            throw new NotFoundException("No current challenges for this user");
        }
    }
}
