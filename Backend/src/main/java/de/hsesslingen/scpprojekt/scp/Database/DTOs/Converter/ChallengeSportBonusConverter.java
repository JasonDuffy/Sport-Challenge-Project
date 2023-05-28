package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportBonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSportBonus;
import de.hsesslingen.scpprojekt.scp.Database.Services.BonusService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts ChallengeSportBonus Entity to DTO and vice-versa
 *
 * @author Robin Hackh
 */
@Component
public class ChallengeSportBonusConverter {

    @Autowired
    ChallengeSportService challengeSportService;
    @Autowired
    BonusService bonusService;
    @Autowired
    ChallengeSportConverter challengeSportConverter;
    @Autowired
    BonusConverter bonusConverter;

    public ChallengeSportBonusDTO convertEntityToDto(ChallengeSportBonus challengeSportBonus){
        ChallengeSportBonusDTO challengeSportBonusDTO = new ChallengeSportBonusDTO();
        challengeSportBonusDTO.setId(challengeSportBonus.getId());
        challengeSportBonusDTO.setChallengeSportID(challengeSportBonus.getChallengeSport().getId());
        challengeSportBonusDTO.setBonusID(challengeSportBonus.getBonus().getId());

        return challengeSportBonusDTO;
    }

    public List<ChallengeSportBonusDTO> convertEntityToDtoList(List<ChallengeSportBonus> challengeSportBonusList) {
        List<ChallengeSportBonusDTO> challengeSportBonusDTOList = new ArrayList<>();

        for (ChallengeSportBonus challengeSportBonus : challengeSportBonusList) {
            challengeSportBonusDTOList.add(convertEntityToDto(challengeSportBonus));
        }

        return challengeSportBonusDTOList;
    }

    public ChallengeSportBonus convertDtoToEntity(ChallengeSportBonusDTO challengeSportBonusDTO) throws NotFoundException {
        ChallengeSportBonus challengeSportBonus = new ChallengeSportBonus();
        challengeSportBonus.setId(challengeSportBonusDTO.getId());
        challengeSportBonus.setChallengeSport(challengeSportConverter.convertDtoToEntity(challengeSportService.get(challengeSportBonusDTO.getChallengeSportID())));
        challengeSportBonus.setBonus(bonusConverter.convertDtoToEntity(bonusService.get(challengeSportBonusDTO.getBonusID())));

        return challengeSportBonus;
    }

    public List<ChallengeSportBonus> convertDtoToEntityList(List<ChallengeSportBonusDTO> challengeSportBonusDTOList) throws NotFoundException {
        List<ChallengeSportBonus> challengeSportBonusList = new ArrayList<>();

        for (ChallengeSportBonusDTO challengeSportBonusDTO : challengeSportBonusDTOList) {
            challengeSportBonusList.add(convertDtoToEntity(challengeSportBonusDTO));
        }

        return challengeSportBonusList;
    }
}
