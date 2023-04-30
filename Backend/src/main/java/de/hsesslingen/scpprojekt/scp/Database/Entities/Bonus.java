package de.hsesslingen.scpprojekt.scp.Database.Entities;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Bonus entity for Database
 * Colums:
 *      id: Primary key
 *      challenge_sport_id: Foreign key of ChallengeSport entity
 *      start_date: Bonus start date (year, month, day, hours, minutes, seconds)
 *      end_date: Bonus end date (year, month, day, hours, minutes, seconds)
 *      factor: Factor of the Bonus
 *      name: Bonus name
 *      description: Bonus description
 *
 * @author Robin Hackh
 */
@Entity
@Table(name = "Bonus")
public class Bonus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "challenge_sport_id")
    private ChallengeSport challengeSport;
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    @Column(name = "end_date", nullable = false)
    private Date endDate;
    @Column(name = "factor", nullable = false)
    private float factor;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;

    public Bonus(){}

    public Bonus(ChallengeSport challengeSport, Date startDate, Date endDate, float factor, String name, String description) {
        this.challengeSport = challengeSport;
        this.startDate = startDate;
        this.endDate = endDate;
        this.factor = factor;
        this.name = name;
        this.description = description;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
