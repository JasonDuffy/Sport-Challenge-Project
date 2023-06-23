package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Sport Repository
 *
 * @author Tom Nguyen Dinh, Jason Patrick Duffy
 */
@Repository
public interface SportRepository extends JpaRepository<Sport,Long> {
    @Transactional
    @Query("select new Sport(s.id, s.name, cs.factor) from Sport s " +
            "join ChallengeSport cs on cs.sport.id=s.id " +
            "join Challenge c on cs.challenge.id=c.id " +
            "where cs.challenge.id=:challengeID")
    public List<Sport> findSportsForChallenge(@Param("challengeID") long challengeID);
}

