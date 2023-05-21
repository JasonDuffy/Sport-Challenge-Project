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
    Long memberID;

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
    public Long getMemberID() {
        return memberID;
    }
    public void setMemberID(Long memberID) {
        this.memberID = memberID;
    }
}
