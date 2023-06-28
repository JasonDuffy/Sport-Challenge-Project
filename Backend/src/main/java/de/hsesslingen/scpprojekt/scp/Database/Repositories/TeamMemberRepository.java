package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    @Query("select tm from TeamMember tm where tm.team.id = :teamID and tm.member.id = :memberID")
    public Optional<TeamMember> findTeamMemberByTeamIdAndMemberId(long teamID, long memberID);

    @Query("select m from Member m join TeamMember tm on tm.member.id = m.id where tm.team.id=:teamID")
    public List<Member> findMembersByTeamId(long teamID);
}
