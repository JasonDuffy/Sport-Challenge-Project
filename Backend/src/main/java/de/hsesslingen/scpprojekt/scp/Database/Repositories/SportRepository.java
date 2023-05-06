package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Sport Repository
 *
 * @author Tom Nguyen Dinh
 */
@Repository
public interface SportRepository extends JpaRepository<Sport,Long> {

}
