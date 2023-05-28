package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO of the ChallengeSportBonus entity.
 * Only holds foreign keys and not whole objects.
 *
 * @author Robin Hackh
 */
public class ChallengeSportBonusDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    private long challengeSportID;
    private long bonusID;

    public ChallengeSportBonusDTO(){}

    public ChallengeSportBonusDTO(long challengeSportID, long bonusID) {
        this.challengeSportID = challengeSportID;
        this.bonusID = bonusID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChallengeSportID() {
        return challengeSportID;
    }

    public void setChallengeSportID(long challengeSportID) {
        this.challengeSportID = challengeSportID;
    }

    public long getBonusID() {
        return bonusID;
    }

    public void setBonusID(long bonusID) {
        this.bonusID = bonusID;
    }
}
