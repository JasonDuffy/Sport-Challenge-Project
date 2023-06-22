package de.hsesslingen.scpprojekt.scp.Database.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Challenge entity for Database
 * Colums:
 *      id: Primary key
 *      email: Unique Email of the user
 *      first_name: User first name
 *      last_name: User first name
 *      communication: Does the user want to receive emails for new bonuses/challenges/etc.?
 *      Image: User image
 *
 * @author Mason Schönherr, Robin Hackh, Jason Patrick Duffy , Tom Nguyen Dinh
 */

//generates table of members
@Entity
@Table(name = "Member")

public class Member{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "communication", nullable = false)
    private Boolean communication;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "image_id", nullable = true)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Image image;

    public Member(){}

    public Member(String email, String firstName, String lastName, Image image, Boolean communication) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.communication = communication;
    }

    //No image provided
    public Member(String email, String firstName, String lastName, Boolean communication) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = null;
        this.communication = communication;
    }

    public long getId() { return id; }

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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getCommunication() {
        return communication;
    }

    public void setCommunication(Boolean communication) {
        this.communication = communication;
    }
}