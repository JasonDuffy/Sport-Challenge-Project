package de.hsesslingen.scpprojekt.scp.Database.DTOs;


/**
 * DTO for ChallengeSport
 *
 * @auth Tom Nguyen Dinh
 */

public class ChallengeSportDTO {
    long id;
    float factor;
    long challengeID;
    long sportID;

    public ChallengeSportDTO(){}

    public ChallengeSportDTO(float factor, long challengeID , long sportID){
        this.factor = factor;
        this.challengeID = challengeID;
        this.sportID = sportID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }
    public long getChallengeID(){
        return challengeID;
    }
    public  void  setChallengeID(long challengeID){
        this.challengeID = challengeID;
    }

    public long getSportID(){
        return challengeID;
    }
    public  void  setSportID(long sportID){
        this.sportID = sportID;
    }
}
