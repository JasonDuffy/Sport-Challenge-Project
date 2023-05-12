package de.hsesslingen.scpprojekt.scp.Database.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * Challenge entity for Database
 * Colums:
 *      id: Primary key
 *      challenge_sport_id: Foreign key of ChallengeSport entity
 *      member_id: Foreign key of Member entity
 *      distance: Distance traveled
 *      date: Date of the activity
 *
 * @author Robin Hackh, Jason Patrick Duffy, Tom Nguyen Dinh
 */
@Entity
@Table(name = "Activity")
public class Activity {

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
    @JoinColumn(name = "member_id")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Member member;
    @Column(name = "distance", nullable = false)
    private float distance;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    public Activity() {}

    public Activity(ChallengeSport challengeSport, Member member, float distance, LocalDateTime date) {
        this.challengeSport = challengeSport;
        this.member = member;
        this.distance = distance;
        this.date = date;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public LocalDateTime getDate() {
        return date;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy,HH:mm")
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
