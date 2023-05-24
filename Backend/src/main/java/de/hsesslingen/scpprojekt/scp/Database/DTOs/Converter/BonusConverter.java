package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts Bonus Entity to DTO and vice-versa
 *
 * @author Jason Patrick Duffy
 */
@Component
public class BonusConverter {
    @Autowired
    ChallengeSportService challengeSportService;

    public BonusDTO convertEntityToDto(Bonus bonus) {
        BonusDTO bonusDTO = new BonusDTO();
        bonusDTO.setDescription(bonus.getDescription());
        bonusDTO.setStartDate(bonus.getStartDate());
        bonusDTO.setEndDate(bonus.getEndDate());
        bonusDTO.setFactor(bonus.getFactor());
        bonusDTO.setId(bonus.getId());
        bonusDTO.setName(bonus.getName());
        bonusDTO.setChallengeSportID(bonus.getChallengeSport().getId());

        return bonusDTO;
    }

    public List<BonusDTO> convertEntityListToDtoList(List<Bonus> bonusList){
        List<BonusDTO> bonusDTOS = new ArrayList<>();

        for(Bonus bonus : bonusList)
            bonusDTOS.add(convertEntityToDto(bonus));

        return bonusDTOS;
    }

    public Bonus convertDtoToEntity(BonusDTO bonusDTO) throws NotFoundException {
        Bonus bonus = new Bonus();
        bonus.setDescription(bonusDTO.getDescription());
        bonus.setStartDate(bonusDTO.getStartDate());
        bonus.setEndDate(bonusDTO.getEndDate());
        bonus.setFactor(bonusDTO.getFactor());
        bonus.setId(bonusDTO.getId());
        bonus.setName(bonusDTO.getName());
        bonus.setChallengeSport(challengeSportService.get(bonusDTO.getChallengeSportID()));

        return bonus;
    }

    public List<Bonus> convertDtoListToEntityList(List<BonusDTO> bonusDTOS) throws NotFoundException {
        List<Bonus> bonuses = new ArrayList<>();

        for(BonusDTO bonusDTO : bonusDTOS)
            bonuses.add(convertDtoToEntity(bonusDTO));

        return bonuses;
    }
}