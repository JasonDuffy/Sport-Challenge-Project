package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TeamMember Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr
 */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember,Long> {

    @Transactional
    public List<TeamMember> findAllByTeamId(long teamID);

    @Transactional
    public List<TeamMember> findAllByMember_Id(long memberID);

}
