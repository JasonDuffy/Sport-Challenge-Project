package de.hsesslingen.scpprojekt.scp.Database.Entities;

import jakarta.persistence.*;

/**
 * ChallengeSport entity for Database
 * Colums:
 *      id: Primary key
 *      team_id: Foreign key of Team entity
 *      member_id: Foreign key of Member entity
 *
 * @author Robin Hackh
 */
@Entity
@Table(name = "TeamMember")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public TeamMember() {}

    public TeamMember(Team team, Member member) {
        this.team = team;
        this.member = member;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
