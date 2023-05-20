package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Member Repository
 *
 * @author Mason Schönherr, Robin Hackh
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByFirstName(String firstName);
    List<Member> findByLastName(String lastName);
}