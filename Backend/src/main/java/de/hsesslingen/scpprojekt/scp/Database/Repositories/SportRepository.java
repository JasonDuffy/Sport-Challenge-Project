package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Challenge Repository
 *
 * @author Tom Nguyen Dinh
 */
public interface SportRepository extends JpaRepository<Sport,Long> {


}
