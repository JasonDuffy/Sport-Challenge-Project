package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Activity Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr, Jason Patrick Duffy
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity,Long> {
    @Transactional
    public List<Activity> findActivitiesByChallengeSport_Id(Long challengeSportID);
    @Transactional
    public List<Activity> findActivitiesByMember_Id(Long memberID);
}
