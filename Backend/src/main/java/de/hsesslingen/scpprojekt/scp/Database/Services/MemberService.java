package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
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
public class MemberService {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberConverter memberConverter;
    /**
     * Returns all members in database
     *
     * @return List of all Bonuses in DB
     */
    public List<MemberDTO> getAll() {
        List<Member> members = memberRepository.findAll();
        return memberConverter.convertEntityListToDtoList(members);
    }

    /**
     * Returns member with given ID in DB
     *
     * @param memberID ID of desired member
     * @return Member with given ID
     * @throws NotFoundException Member can not be found
     */
    public MemberDTO get(Long memberID) throws NotFoundException {
        Optional<Member> member = memberRepository.findById(memberID);
        if(member.isPresent())
            return memberConverter.convertEntityToDto(member.get());
        throw new NotFoundException("Member with ID " + memberID + " is not present in DB.");
    }

    /**
     * Returns member currently logged in
     *
     * @return Member that is logged in
     * @throws NotFoundException Member can not be found
     */
    public MemberDTO getByEmail(String email) throws NotFoundException {
        Member member = memberRepository.findMemberByEmail(email);
        if(member == null)
            throw new NotFoundException("Member with email " + email + " is not present in DB.");
        return memberConverter.convertEntityToDto(member);
    }

    /**
     * Adds a given member to the DB
     *
     * @param memberDTO MemberDTO object to be added to DB
     * @return Added member object
     */
    public MemberDTO add(MemberDTO memberDTO) throws AlreadyExistsException, NotFoundException {
        Member member = memberConverter.convertDtoToEntity(memberDTO);
        if(!memberRepository.existsMemberByEmail(member.getEmail()))
            return memberConverter.convertEntityToDto(memberRepository.save(new Member(member.getEmail(), member.getFirstName(), member.getLastName(), member.getImage())));
        throw new AlreadyExistsException("Member with email " + member.getEmail() + " already exists in DB!");
    }

    /**
     * Updates a member
     *
     * @param memberID ID of the member to be updated
     * @param member   Member object that overwrites the old member
     * @return Updated bonus object
     */
    public MemberDTO update(Long memberID, MemberDTO member) throws NotFoundException {
        MemberDTO newMember = get(memberID);

        newMember.setEmail(member.getEmail());
        newMember.setFirstName(member.getFirstName());
        newMember.setLastName(member.getLastName());
        newMember.setImageID(member.getImageID());
        newMember.setUserID(memberID);

        return memberConverter.convertEntityToDto(memberRepository.save(memberConverter.convertDtoToEntity(newMember)));
    }

    /**
     * Deletes a specific member from the DB
     *
     * @param memberID ID of the bonus to be deleted
     */
    public void delete(Long memberID) throws NotFoundException {
        get(memberID);
        memberRepository.deleteById(memberID);
    }

    /**
     * Deletes all members from the DB
     */
    public void deleteAll() {
        memberRepository.deleteAll();
    }
}
