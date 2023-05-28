package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ChallengeSport Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr
 */
@Repository
public interface ChallengeSportRepository extends JpaRepository<ChallengeSport,Long> {

    List<ChallengeSport> findChallengeSportByChallenge_Id(long challengeID);

}
