package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Activity Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr, Jason Patrick Duffy
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity,Long> {
    @Transactional
    public List<Activity> findActivitiesByChallengeSport_Id(Long challengeSportID);
    @Transactional
    public List<Activity> findActivitiesByMember_Id(Long memberID);

    @Transactional
    @Query("SELECT a FROM Activity a " +
            "JOIN ChallengeSport cs ON cs.id=a.challengeSport.id " +
            "JOIN Team t ON t.challenge.id=cs.challenge.id " +
            "JOIN TeamMember tm ON tm.team.id=t.id AND tm.member.id=a.member.id " +
            "WHERE cs.challenge.id = :challengeID")
    public List<Activity> findActivitiesByChallenge_ID(@Param("challengeID") long challengeID); // Only returns activities of members that are part of a team

    @Transactional
    @Query("SELECT a FROM Activity a JOIN a.challengeSport cs where cs.challenge.id = :challengeID AND a.member.id = :memberID")
    public List<Activity> findActivitiesByChallenge_IDAndMember_ID(@Param("challengeID") long challengeID, @Param("memberID") long memberID);
}
