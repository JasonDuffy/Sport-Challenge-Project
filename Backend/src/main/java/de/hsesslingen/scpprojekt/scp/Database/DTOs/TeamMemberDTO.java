package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for TeamMember
 * 
 * @auth Tom Nguyen Dinh
 */
public class TeamMemberDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    long teamID;
    long memberID;

    public TeamMemberDTO(){}

    public TeamMemberDTO(long teamID, long memberID){
        this.teamID =teamID;
        this.memberID =memberID;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getTeamID() {
        return teamID;
    }
    public void setTeamID(long teamID) {
        this.teamID = teamID;
    }
    public long getMemberID() {
        return memberID;
    }
    public void setMemberID(long memberID) {
        this.memberID = memberID;
    }
}
