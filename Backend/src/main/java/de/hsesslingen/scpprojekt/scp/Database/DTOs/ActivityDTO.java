package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO of the Activity entity.
 * Only holds foreign keys and not whole objects.
 *
 * @author Jason Patrick Duffy
 */
public class ActivityDTO  {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    long challengeSportID;
    long memberID;
    float distance;
    LocalDateTime date;
    Float totalDistance;

    public ActivityDTO() {}

    public ActivityDTO(long id, long challengeSportID, long memberID, float distance, LocalDateTime date) {
        this.id = id;
        this.challengeSportID = challengeSportID;
        this.memberID = memberID;
        this.distance = distance;
        this.date = date;
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

    public long getMemberID() {
        return memberID;
    }

    public void setMemberID(long memberID) {
        this.memberID = memberID;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public LocalDateTime getDate() {
        return date;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Float totalDistance) {
        this.totalDistance = totalDistance;
    }

}
