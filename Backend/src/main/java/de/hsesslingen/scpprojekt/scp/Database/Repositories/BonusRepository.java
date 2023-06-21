package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Bonus Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr, Jason Patrick Duffy
 */
@Repository
public interface BonusRepository extends JpaRepository<Bonus,Long> {
    @Transactional
    @Query("SELECT b FROM Bonus b JOIN ChallengeSportBonus csb on csb.bonus.id = b.id  join ChallengeSport cs on cs.id = csb.challengeSport.id where cs.challenge.id = :challengeID")
    public List<Bonus> findBonusesByChallengeID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("SELECT DISTINCT b FROM Bonus b " +
            "JOIN ChallengeSportBonus csb ON csb.bonus.id=b.id " +
            "JOIN ChallengeSport cs ON csb.challengeSport.id=cs.id " +
            "WHERE cs.challenge.id = :challengeID " +
            "and b.startDate <= current date " +
            "and b.endDate > current date " +
            "order by b.startDate asc")
    public List<Bonus> findCurrentBonusesByChallengeID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("SELECT DISTINCT b FROM Bonus b " +
            "JOIN ChallengeSportBonus csb ON csb.bonus.id=b.id " +
            "JOIN ChallengeSport cs ON csb.challengeSport.id=cs.id " +
            "WHERE cs.challenge.id = :challengeID " +
            "and b.startDate <= current date " +
            "and b.endDate <= current date " +
            "order by b.startDate asc")
    public List<Bonus> findPastBonusesByChallengeID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("SELECT DISTINCT b FROM Bonus b " +
            "JOIN ChallengeSportBonus csb ON csb.bonus.id=b.id " +
            "JOIN ChallengeSport cs ON csb.challengeSport.id=cs.id " +
            "WHERE cs.challenge.id = :challengeID " +
            "and b.startDate > current date " +
            "and b.endDate > current date " +
            "order by b.startDate asc")
    public List<Bonus> findFutureBonusesByChallengeID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("select b from Bonus b " +
            "join ChallengeSportBonus csb on csb.bonus.id=b.id " +
            "join ChallengeSport cs on cs.id=csb.challengeSport.id " +
            "where cs.sport.id = :sportID and cs.challenge.id = :challengeID " +
            "and b.endDate > current date and b.startDate <= current date")
    public List<Bonus> findCurrentBonusesByChallengeIDAndSportID(@Param("challengeID") long challengeID, @Param("sportID") long sportID);

    @Transactional
    @Query("select b from Bonus b " +
            "join ChallengeSportBonus csb on csb.bonus.id=b.id " +
            "join ChallengeSport cs on cs.id=csb.challengeSport.id " +
            "where cs.sport.id = :sportID and cs.challenge.id = :challengeID " +
            "and b.endDate > :dateTime and b.startDate <= :dateTime")
    public List<Bonus> findBonusesByChallengeIDAndSportIDAtSpecificTime(@Param("challengeID") long challengeID, @Param("sportID") long sportID, @Param("dateTime")LocalDateTime time);
}
