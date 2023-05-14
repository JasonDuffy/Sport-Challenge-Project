package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
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
}