package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Bonus Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr, Jason Patrick Duffy
 */
@Repository
public interface BonusRepository extends JpaRepository<Bonus,Long> {
    @Transactional
    @Query("SELECT b FROM Bonus b JOIN b.challengeSport cs where cs.challenge.id = :challengeID order by b.startDate asc")
    public List<Bonus> findBonusesByChallengeID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("SELECT b FROM Bonus b JOIN b.challengeSport cs " +
            "where cs.challenge.id = :challengeID " +
            "and b.startDate <= current date " +
            "and b.endDate > current date " +
            "order by b.startDate asc")
    public List<Bonus> findCurrentBonusesByChallengeID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("SELECT b FROM Bonus b JOIN b.challengeSport cs " +
            "where cs.challenge.id = :challengeID " +
            "and b.startDate <= current date " +
            "and b.endDate <= current date " +
            "order by b.startDate asc")
    public List<Bonus> findPastBonusesByChallengeID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("SELECT b FROM Bonus b JOIN b.challengeSport cs " +
            "where cs.challenge.id = :challengeID " +
            "and b.startDate > current date " +
            "and b.endDate > current date " +
            "order by b.startDate asc")
    public List<Bonus> findFutureBonusesByChallengeID(@Param("challengeID") long challengeID);
}
