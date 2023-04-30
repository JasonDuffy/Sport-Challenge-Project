package de.hsesslingen.scpprojekt.scp.Database.Entities;

import jakarta.persistence.*;

/**
 * Challenge entity for Database
 * Colums:
 *      id: Primary key
 *      challenge_sport_id: Foreign key of ChallengeSport entity
 *      member_id: Foreign key of Member entity
 *      distance: Distance traveled
 *
 * @author Robin Hackh
 */
@Entity
@Table(name = "Activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "challenge_sport_id")
    private ChallengeSport challengeSport;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(name = "distance", nullable = false)
    private float distance;

    public Activity() {}

    public Activity(ChallengeSport challengeSport, Member member, float distance) {
        this.challengeSport = challengeSport;
        this.member = member;
        this.distance = distance;
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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
