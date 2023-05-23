package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.SportService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts ChallengeSport Entity to DTO and vice-versa
 *
 * @author Tom Nguyen Dinh
 */
@Component
public class ChallengeSportConverter {

    @Autowired
    SportService sportService;
    @Autowired
    ChallengeService challengeService;

    public ChallengeSportDTO convertEntityToDto(ChallengeSport challengeSport) {
        ChallengeSportDTO ChallengeSportDTO = new ChallengeSportDTO();
        ChallengeSportDTO.setId(challengeSport.getId());
        ChallengeSportDTO.setChallengeID(challengeSport.getChallenge().getId());
        ChallengeSportDTO.setSportID(challengeSport.getSport().getId());
        ChallengeSportDTO.setFactor(challengeSport.getFactor());
        return ChallengeSportDTO;
    }

    public List<ChallengeSportDTO> convertEntityListToDtoList(List<ChallengeSport> challengeSportList){
        List<ChallengeSportDTO> challengeSportDTOS = new ArrayList<>();

        for(ChallengeSport challengeSport : challengeSportList)
            challengeSportDTOS.add(convertEntityToDto(challengeSport));

        return challengeSportDTOS;
    }

    public ChallengeSport convertDtoToEntity(ChallengeSportDTO challengeSportDTO) throws NotFoundException {
        ChallengeSport challengeSport = new ChallengeSport();
        challengeSport.setId(challengeSportDTO.getId());
        challengeSport.setChallenge(challengeService.get(challengeSportDTO.getChallengeID()));
        challengeSport.setSport(sportService.get(challengeSportDTO.getSportID()));
        challengeSport.setFactor(challengeSportDTO.getFactor());
        return challengeSport;
    }

    public List<ChallengeSport> convertDtoListToEntityList(List<ChallengeSportDTO> challengeSportDTOS) throws NotFoundException {
        List<ChallengeSport> challengeSportList = new ArrayList<>();

        for(ChallengeSportDTO challengeSportDTO : challengeSportDTOS)
            challengeSportList.add(convertDtoToEntity(challengeSportDTO));

        return challengeSportList;
    }
}
