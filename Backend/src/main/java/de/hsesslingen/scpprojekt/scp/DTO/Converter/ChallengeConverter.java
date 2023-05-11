package de.hsesslingen.scpprojekt.scp.DTO.Converter;

import de.hsesslingen.scpprojekt.scp.DTO.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Service.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChallengeConverter {
    @Autowired
    ChallengeService challengeService;
    @Autowired
    ImageStorageService imageStorageService;

    public ChallengeDTO convertEntityToDto(Challenge challenge) {
        ChallengeDTO  ChallengeDTO = new ChallengeDTO();
        ChallengeDTO .setId(challenge.getId());
        ChallengeDTO .setName(challenge.getName());
        ChallengeDTO .setImageID(challenge.getImage().getId());
        ChallengeDTO .setDescription(challenge.getDescription());
        ChallengeDTO .setStartDate(challenge.getStartDate());
        ChallengeDTO .setEndDate(challenge.getEndDate());
        ChallengeDTO .setTargetDistance(challenge.getTargetDistance());
        return ChallengeDTO ;
    }

    public List<ChallengeDTO > convertEntityListToDtoList(List<Challenge> challengeList){
        List<ChallengeDTO > ChallengeDTOList = new ArrayList<>();

        for(Challenge challenge : challengeList)
            ChallengeDTOList.add(convertEntityToDto(challenge));

        return ChallengeDTOList;
    }

    public Challenge convertDtoToEntity(ChallengeDTO  challengeDTO ) throws NotFoundException {
        Challenge challenge = new Challenge();
        challenge.setId(challengeDTO.getId());
        challenge.setName(challengeDTO.getName());
        challenge.setImage(imageStorageService.get((challengeDTO.getImageID())));
        challenge.setDescription(challengeDTO.getDescription());
        challenge.setStartDate(challengeDTO.getStartDate());
        challenge.setEndDate(challengeDTO.getEndDate());
        challenge.setTargetDistance(challengeDTO.getTargetDistance());
        return challenge;
    }

    public List<Challenge> convertDtoListToEntityList(List<ChallengeDTO > challengeDTOList) throws NotFoundException {
        List<Challenge> challengeList = new ArrayList<>();
        for(ChallengeDTO  newChallengeDTO : challengeDTOList)
            challengeList.add(convertDtoToEntity(newChallengeDTO ));

        return challengeList;
    }
}
