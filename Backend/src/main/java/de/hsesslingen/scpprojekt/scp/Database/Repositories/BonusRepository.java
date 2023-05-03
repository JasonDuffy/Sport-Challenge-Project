package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Bonus Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr
 */
@Repository
public interface BonusRepository extends JpaRepository<Bonus,Long> {

}
