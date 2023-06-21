package de.hsesslingen.scpprojekt.scp.Database.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Challenge entity for Database
 * Colums:
 *      id: Primary key
 *      name: Team name
 *      image_id: Foreign key of Image entity
 *      challenge_id: Foreign key of Challenge entity
 *
 * @author Robin Hackh, Tom Nguyen Dinh
 */
@Entity
@Table(name = "Team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "image_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Image image;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public Team() {}

    public Team(String name, Image image, Challenge challenge) {
        this.name = name;
        this.image = image;
        this.challenge = challenge;
    }

    public Team(String name, Challenge challenge) {
        this.name = name;
        this.image = null;
        this.challenge = challenge;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}
