package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSportBonus;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ChallengeSportBonus Repository
 *
 * @author Robin Hackh, Tom Nguyen Dinh
 */

@Repository
public interface ChallengeSportBonusRepository extends JpaRepository<ChallengeSportBonus, Long> {

    @Transactional
    public List<ChallengeSportBonus> findAllByBonusId(long bonusID);

    @Transactional
    @Query("select cs from ChallengeSport cs join ChallengeSportBonus csb on csb.challengeSport.id  = cs.id where csb.bonus.id=:bonusID")
    public List<ChallengeSport> findChallengeSportForBonus(long bonusID);
}
