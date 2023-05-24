package de.hsesslingen.scpprojekt.scp.Database.DTOs;


import com.fasterxml.jackson.annotation.JsonProperty;

public class TeamDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    String name;
    long imageID;
    long challengeID;

    public TeamDTO() {}

    public TeamDTO(String name , long imageID, long challengeID) {
        this.name = name;
        this.imageID = imageID;
        this.challengeID = challengeID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getImageID() {
        return imageID;
    }

    public void setImageID(long imageID) {
        this.imageID = imageID;
    }

    public long getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(long challengeID) {
        this.challengeID = challengeID;
    }


}
