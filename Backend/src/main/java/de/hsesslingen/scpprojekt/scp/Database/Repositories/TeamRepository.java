package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Team Repository
 *
 * @author Tom Nguyen Dinh, Mason Schönherr, Jason Patrick Duffy
 */
@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {
    @Transactional
    public List<Team> findTeamsByChallenge_Id(long challengeID);

    @Transactional
    public void deleteAllByChallenge_Id(long challengeID);

    @Transactional
    @Query("select m from Member m join TeamMember tm on m.id= tm.member.id join Team t on tm.team.id = t.id where t.id = :teamID ")
    List<Member> findMembersByTeamID(@Param("teamID") long teamID);

    @Transactional
    @Query("select count(*) from TeamMember tm group by tm.team.id having tm.team.id=:teamID")
    public int countMembersOfTeam(@Param("teamID") long teamID);
}
