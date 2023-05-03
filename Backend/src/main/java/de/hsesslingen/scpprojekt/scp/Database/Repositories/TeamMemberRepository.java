package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * TeamMember Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr
 */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember,Long> {

}
