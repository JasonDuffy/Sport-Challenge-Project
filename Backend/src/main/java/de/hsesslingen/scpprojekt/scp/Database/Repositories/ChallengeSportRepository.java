package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ChallengeSport Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr
 */
@Repository
public interface ChallengeSportRepository extends JpaRepository<ChallengeSport,Long> {
    @Transactional
    List<ChallengeSport> findChallengeSportByChallenge_Id(long challengeID);

    @Transactional
    ChallengeSport findChallengeSportByChallenge_IdAndSport_Id(long challengeID, long sportID);

}
