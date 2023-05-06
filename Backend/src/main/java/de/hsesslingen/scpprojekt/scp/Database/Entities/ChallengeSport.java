package de.hsesslingen.scpprojekt.scp.Database.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

/**
 * ChallengeSport entity for Database
 * Colums:
 *      id: Primary key
 *      factor: Factor for the Sport in this Challenge
 *      challenge_id: Foreign key of Challenge entity
 *      sport_id: Foreign key of Sport entity
 *
 * @author Robin Hackh
 */
@Entity
@Table(name = "ChallengeSport")
public class ChallengeSport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;
    @Column(name = "factor", nullable = false)
    private float factor;
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;
    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Sport sport;

    public ChallengeSport() {}

    public ChallengeSport(float factor, Challenge challenge, Sport sport) {
        this.factor = factor;
        this.challenge = challenge;
        this.sport = sport;
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

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }
}