package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service of the Member entity
 *
 * @author Jason Patrick Duffy
 */

@Service
public class MemberService implements MemberServiceInterface {
    @Autowired
    MemberRepository memberRepository;
    /**
     * Returns all members in database
     *
     * @return List of all Bonuses in DB
     */
    @Override
    public List<Member> getAll() {
        return memberRepository.findAll();
    }

    /**
     * Returns member with given ID in DB
     *
     * @param memberID ID of desired member
     * @return Member with given ID
     * @throws NotFoundException Member can not be found
     */
    @Override
    public Member get(Long memberID) throws NotFoundException {
        Optional<Member> member = memberRepository.findById(memberID);
        if(member.isPresent())
            return member.get();
        throw new NotFoundException("Member with ID " + memberID + " is not present in DB.");
    }

    /**
     * Adds a given member to the DB
     *
     * @param member Member object to be added to DB
     * @return Added member object
     */
    @Override
    public Member add(Member member) throws NotFoundException {
        return memberRepository.save(new Member(member.getEmail(), member.getFirstName(), member.getLastName(), member.getImage()));
    }

    /**
     * Updates a member
     *
     * @param memberID ID of the member to be updated
     * @param member   Member object that overwrites the old member
     * @return Updated bonus object
     */
    @Override
    public Member update(Long memberID, Member member) throws NotFoundException {
        Member newMember = get(memberID);

        newMember.setEmail(member.getEmail());
        newMember.setFirstName(member.getFirstName());
        newMember.setLastName(member.getLastName());
        newMember.setImage(member.getImage());

        return memberRepository.save(newMember);
    }

    /**
     * Deletes a specific member from the DB
     *
     * @param memberID ID of the bonus to be deleted
     */
    @Override
    public void delete(Long memberID) throws NotFoundException {
        get(memberID);
        memberRepository.deleteById(memberID);
    }

    /**
     * Deletes all members from the DB
     */
    @Override
    public void deleteAll() {
        memberRepository.deleteAll();
    }
}
