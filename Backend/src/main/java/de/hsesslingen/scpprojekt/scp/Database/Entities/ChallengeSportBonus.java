package de.hsesslingen.scpprojekt.scp.Database.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * ChallengeSportBonus entity for Database
 * Colums:
 *      id: Primary key
 *      challenge_sport_id: Foreign key of ChallengeSport entity
 *      bonus_id: Foreign key of Bonus entity
 *
 * @author Robin Hackh
 */
@Entity
@Table(name = "ChallengeSportBonus", uniqueConstraints = @UniqueConstraint(columnNames = {"challenge_sport_id", "bonus_id"}))
public class ChallengeSportBonus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "challenge_sport_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ChallengeSport challengeSport;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "bonus_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Bonus bonus;

    public ChallengeSportBonus(){}

    public ChallengeSportBonus(ChallengeSport challengeSport, Bonus bonus) {
        this.challengeSport = challengeSport;
        this.bonus = bonus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ChallengeSport getChallengeSport() {
        return challengeSport;
    }

    public void setChallengeSport(ChallengeSport challengeSport) {
        this.challengeSport = challengeSport;
    }

    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
    }
}
