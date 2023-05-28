package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSportBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ChallengeSportBonus Repository
 *
 * @author Robin Hackh
 */

@Repository
public interface ChallengeSportBonusRepository extends JpaRepository<ChallengeSportBonus, Long> {
}
