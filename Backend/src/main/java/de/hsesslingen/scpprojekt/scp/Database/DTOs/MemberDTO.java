package de.hsesslingen.scpprojekt.scp.Database.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO of the Member  entity.
 * Only holds foreign keys and not whole objects.
 *
 * @author Jason Patrick Duffy
 */
public class MemberDTO {
    String email, firstName, lastName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Long userID;
    Long imageID;

    public MemberDTO() {}

    public MemberDTO(String email, String firstName, String lastName, Long userID, Long imageID) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
        this.imageID = imageID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getImageID() {
        return imageID;
    }

    public void setImageID(Long imageID) {
        this.imageID = imageID;
    }
}
