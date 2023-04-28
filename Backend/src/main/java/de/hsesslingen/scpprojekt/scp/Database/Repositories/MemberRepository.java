package de.hsesslingen.scpprojekt.scp.Database.Repositories;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Irgendwas mit Repository
 * TODO: @Mason Kommentiere die Klasse ordentlich - siehe SAML2Controller
 * @author Mason Schönherr
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id); //Optional for the isPresent() method

    List<Member> findByFirstName(String firstName);

    List<Member> findByLastName(String lastName);

}