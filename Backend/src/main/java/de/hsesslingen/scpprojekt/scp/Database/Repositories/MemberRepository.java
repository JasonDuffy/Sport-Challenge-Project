package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Member Repository
 *
 * @author Mason Schönherr, Robin Hackh, Jason Patrick DUffy
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByFirstName(String firstName);
    List<Member> findByLastName(String lastName);

    @Transactional
    Member findMemberByEmail(String email);

    @Transactional
    Boolean existsMemberByEmail(String email);

    @Transactional
    @Query("select m.email from Member m where m.communication = true")
    List<String> findAllEmails();

    @Transactional
    @Query("select m from Member m join TeamMember tm on m.id=tm.member.id join Team t on tm.team.id=t.id where t.challenge.id = :challengeID")
    List<Member> findMembersByChallenge_ID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("select m.email from Member m join TeamMember tm on m.id=tm.member.id join Team t on tm.team.id=t.id where t.challenge.id = :challengeID and m.communication = true")
    List<String> findMembersEmailByChallengeID(@Param("challengeID") long challengeID);

    @Transactional
    @Query("select m from Member m join Activity a on a.member.id=m.id where a.date = (select max(act.date) from Activity act where act.member.id=m.id) and a.date <= (current date - 7) and m.communication = true")
    List<Member> findMembersWhoseLastActivityWasMoreThanOneWeekAgo();

    @Transactional
    @Query("select t from Team t join TeamMember tm on t.id= tm.team.id join Member m on tm.member.id = m.id where m.id = :memberID ")
    List<Team> findTeamsByMemberID(@Param("memberID") long memberID);
}