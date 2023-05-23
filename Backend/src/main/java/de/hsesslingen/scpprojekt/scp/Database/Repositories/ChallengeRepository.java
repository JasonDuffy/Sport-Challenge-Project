package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Challenge Repository
 *
 * @author Robin Hackh
 */

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    /**
     * @param name Name of the Challenges that should be returned
     * @return A list of Challenges with the given name
     */
    List<Challenge> findByName(String name);

    @Transactional
    @Query("SELECT t.challenge.id  from Member m join TeamMember tm on tm.member.id = m.id join Team t on tm.team.id = t.id where m.id = :memberID")
    public List<Long> findChallengeIDsByMemberID(@Param("memberID") long memberID);
}
