package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Challenge Repository
 *
 * @author Robin Hackh
 */

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    /**
     * @param name Name of the Challenges that should be returned
     * @return A list of Challenges with the given name
     */
    List<Challenge> findByName(String name);
}
