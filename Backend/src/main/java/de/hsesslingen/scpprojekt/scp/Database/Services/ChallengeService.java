package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service of the Challenge
 *
 * @auth Tom Nguyen Dinh
 */
@Service
public class ChallengeService {

    @Autowired
    ChallengeRepository challengeRepository;
    @Autowired
    ImageStorageService imageStorageService;
    @Autowired
    ChallengeConverter challengeConverter;
    @Autowired
    ChallengeSportRepository challengeSportRepository;
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


    public ChallengeDTO add(MultipartFile file, long sportId[], float sportFactor[], ChallengeDTO challenge) throws NotFoundException {
        try {
            Image image = imageStorageService.store(file);
            Challenge newchallenge = challengeConverter.convertDtoToEntity(challenge);
            newchallenge.setImage(image);
            Challenge savedChallenge = challengeRepository.save(newchallenge);

            for (int i = 0; i < sportId.length; i++) {
                challengeSportRepository.save(new ChallengeSport(sportFactor[i], newchallenge, sportService.get(sportId[i])));
            }
            return challengeConverter.convertEntityToDto(savedChallenge);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
