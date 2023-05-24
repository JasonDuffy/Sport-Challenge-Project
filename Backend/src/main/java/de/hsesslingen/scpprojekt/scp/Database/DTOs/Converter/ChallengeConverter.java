package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts  Challenge Entity to DTO and vice versa
 *
 * @auth Tom Nguyen Dinh
 */
@Component
public class ChallengeConverter {
    @Autowired
    ImageStorageService imageStorageService;

    /**
     * Converts Entity Challenge to DTo
     * @param challenge Challenge object
     * @return Dto of challenge
     */
    public ChallengeDTO convertEntityToDto(Challenge challenge) {
        ChallengeDTO  ChallengeDTO = new ChallengeDTO();
        ChallengeDTO.setId(challenge.getId());
        ChallengeDTO.setName(challenge.getName());
        try{
            ChallengeDTO.setImageID(challenge.getImage().getId());
        }catch (NullPointerException e){
            ChallengeDTO.setImageID(null);
        }
        ChallengeDTO.setDescription(challenge.getDescription());
        ChallengeDTO.setStartDate(challenge.getStartDate());
        ChallengeDTO.setEndDate(challenge.getEndDate());
        ChallengeDTO.setTargetDistance(challenge.getTargetDistance());
        return ChallengeDTO ;
    }

    /**
     * Converts Entity List to DTO List
     *
     * @param challengeList List of Challenges
     * @return List of Challenges as DTO
     */
    public List<ChallengeDTO > convertEntityListToDtoList(List<Challenge> challengeList){
        List<ChallengeDTO > ChallengeDTOList = new ArrayList<>();

        for(Challenge challenge : challengeList)
            ChallengeDTOList.add(convertEntityToDto(challenge));

        return ChallengeDTOList;
    }

    /**
     * Converts DTO Challenge to Entity
     *
     * @param challengeDTO Dto of Challenge
     * @return Challenge Entity
     * @throws NotFoundException Not Found Challenge
     */
    public Challenge convertDtoToEntity(ChallengeDTO  challengeDTO ) {
        Challenge challenge = new Challenge();
        challenge.setId(challengeDTO.getId());
        challenge.setName(challengeDTO.getName());
        try {
            challenge.setImage(imageStorageService.get((challengeDTO.getImageID())));
        }catch (NullPointerException|NotFoundException e){
            challenge.setImage(null);
        }
        challenge.setDescription(challengeDTO.getDescription());
        challenge.setStartDate(challengeDTO.getStartDate());
        challenge.setEndDate(challengeDTO.getEndDate());
        challenge.setTargetDistance(challengeDTO.getTargetDistance());
        return challenge;
    }

    /**
     * Converts Dto List of challenges to Entity List
     *
     * @param challengeDTOList DTo list of challenges
     * @return Entity List of Challenge
     * @throws NotFoundException Not found Challenge
     */
    public List<Challenge> convertDtoListToEntityList(List<ChallengeDTO > challengeDTOList) throws NotFoundException {
        List<Challenge> challengeList = new ArrayList<>();
        for(ChallengeDTO  newChallengeDTO : challengeDTOList)
            challengeList.add(convertDtoToEntity(newChallengeDTO ));

        return challengeList;
    }
}
