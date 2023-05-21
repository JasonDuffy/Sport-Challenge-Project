package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;

import java.time.LocalDateTime;

public class ChallengeDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long Id;
    String name;
    String description;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Long imageID;
    float targetDistance;

    public ChallengeDTO() {}

    public ChallengeDTO(String name, String description, LocalDateTime startDate, LocalDateTime endDate, long imageID, float targetDistance) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageID = imageID;
        this.targetDistance = targetDistance;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        this.Id = Id;
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

    public long getImageID() {
        return imageID;
    }

    public void setImageID(long imageID) {
        this.imageID = imageID;
    }

    public float getTargetDistance() {
        return targetDistance;
    }

    public void setTargetDistance(float targetDistance) {
        this.targetDistance = targetDistance;
    }
}
