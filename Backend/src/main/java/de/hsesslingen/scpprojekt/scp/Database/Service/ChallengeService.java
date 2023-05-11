package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.DTO.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.DTO.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 *
 */
@Service
public class ChallengeService {

    @Autowired
    ChallengeRepository challengeRepository;
    @Autowired
    ImageStorageService imageStorageService;


/*
    public List<ChallengeDTO> getAll() {
        List<Challenge> challengeListList = challengeRepository.findAll();
        return  challengeConverter.convertEntityListToDtoList(challengeListList);

    }
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
}
