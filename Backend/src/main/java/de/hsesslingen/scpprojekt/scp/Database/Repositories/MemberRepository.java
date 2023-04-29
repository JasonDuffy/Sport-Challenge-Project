package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Irgendwas mit Repository
 * TODO: @Mason Kommentiere die Klasse ordentlich - siehe SAML2Controller
 * @author Mason Schönherr
 */

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByFirstName(String firstName);

    List<Member> findByLastName(String lastName);
}