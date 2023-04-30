package de.hsesslingen.scpprojekt.scp.Database.Entities;

import jakarta.persistence.*;

/**
 * Challenge entity for Database
 * Colums:
 *      email: Primary key (Email of the user)
 *      first_name: User first name
 *      last_name: User first name
 *
 * @author Mason Schönherr, Robin Hackh
 */

//generates table of members
@Entity
@Table(name = "Member")

public class Member{

    @Id
    private String email;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;

    public Member(){}

    public Member(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getFirstName(){
        return firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getLastName(){
        return lastName;
    }
}