package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    @Query("SELECT c from Challenge c join Team t on t.challenge.id = c.id join TeamMember tm on tm.team.id = t.id join Member m on m.id = tm.member.id where m.id = :memberID and c.startDate <= :date and c.endDate >= :date")
    public List<Challenge> findChallengesByMemberIDAndDate(@Param("memberID") long memberID, @Param("date") LocalDateTime date);
}
