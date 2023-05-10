package de.hsesslingen.scpprojekt.scp.Database.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO of the Bonus entity.
 * Only holds foreign keys and not whole objects.
 *
 * @author Jason Patrick DUffy
 */
public class BonusDTO {
    private long id;
    private Long challengeSportID;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private float factor;
    private String name;

    public BonusDTO(){}

    public BonusDTO(long id, Long challengeSportID, LocalDateTime startDate, LocalDateTime endDate, float factor, String name, String description) {
        this.id = id;
        this.challengeSportID = challengeSportID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.factor = factor;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getChallengeSportID() {
        return challengeSportID;
    }

    public void setChallengeSportID(Long challengeSportID) {
        this.challengeSportID = challengeSportID;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public LocalDateTime getStartDate() {
        return startDate;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public LocalDateTime getEndDate() {
        return endDate;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;
}
