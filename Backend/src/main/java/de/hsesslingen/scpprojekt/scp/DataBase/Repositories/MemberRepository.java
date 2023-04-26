package de.hsesslingen.scpprojekt.scp.DataBase.Repositories;

import de.hsesslingen.scpprojekt.scp.DataBase.Entities.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id); //Optional for the isPresent() method

    List<Member> findByFirstName(String firstName);

    List<Member> findByLastName(String lastName);

}