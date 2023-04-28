package de.hsesslingen.scpprojekt.scp.Database.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import org.springframework.data.annotation.Id;

/**
 * Member Entity
 * TODO: @Mason Kommentiere die Klasse ordentlich - siehe SAML2Controller
 * @author Mason Schönherr
 */

//generates table of members
@Entity
@Table(name = "Member")

public class Member{

    @jakarta.persistence.Id
    @Id
    @GeneratedValue
    private long
            id;

   /* @TableGenerator(name = "Member",
            table = "id",
            pkColumnName = "first_name",
            valueColumnName = "last_name") */

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }
}