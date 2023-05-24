package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Team Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr
 */
@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {

}
