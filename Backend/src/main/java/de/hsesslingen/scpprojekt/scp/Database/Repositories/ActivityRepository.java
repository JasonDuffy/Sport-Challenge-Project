package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Activity Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity,Long> {

}
