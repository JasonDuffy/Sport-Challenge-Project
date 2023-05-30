package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service of the Member entity
 *
 * @author Jason Patrick Duffy, Tom Nguyen Dinh
 */

@Service
public class MemberService {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    @Lazy
    MemberConverter memberConverter;
    @Autowired
    @Lazy
    ActivityConverter activityConverter;
    @Autowired
    @Lazy
    TeamConverter teamConverter;

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
     * Returns all email addresses of members opted into communication
     * @return List of email addresses of members opted into communication
     */
    public List<String> getAllEmails(){
        return memberRepository.findAllEmails();
    }

    /**
     * Returns member with given ID in DB
     *
     * @param memberID ID of desired member
     * @return Member with given ID
     * @throws NotFoundException Member can not be found
     */
    public MemberDTO get(long memberID) throws NotFoundException {
        Optional<Member> member = memberRepository.findById(memberID);
        if(member.isPresent())
            return memberConverter.convertEntityToDto(member.get());
        throw new NotFoundException("Member with ID " + memberID + " is not present in DB.");
    }

    /**
     * Returns the member corresponding to the given email
     * @param email Email Address of the member
     * @return Member corresponding to the email address given
     * @throws NotFoundException Thrown when no member with given email address is found
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
            return memberConverter.convertEntityToDto(memberRepository.save(new Member(member.getEmail(), member.getFirstName(), member.getLastName(), member.getImage(), member.getCommunication())));
        throw new AlreadyExistsException("Member with email " + member.getEmail() + " already exists in DB!");
    }

    /**
     * Updates a member
     *
     * @param memberID ID of the member to be updated
     * @param member   Member object that overwrites the old member
     * @return Updated bonus object
     */
    public MemberDTO update(long memberID, MemberDTO member) throws NotFoundException {
        MemberDTO newMember = get(memberID);

        newMember.setEmail(member.getEmail());
        newMember.setFirstName(member.getFirstName());
        newMember.setLastName(member.getLastName());
        newMember.setImageID(member.getImageID());
        newMember.setCommunication(member.getCommunication());
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

    /**
     * Return all activities for given User ID
     * @param userID User ID for returned activities
     * @return All activities by the given user ID
     */
    public List<ActivityDTO> getActivitiesForUser(Long userID){
        return activityConverter.convertEntityListToDtoList(activityRepository.findActivitiesByMember_Id(userID));
    }

    /**
     * Returns all activities of a given user in a given challenge
     * @param challengeID Challenge ID of the requested activities
     * @param userID User ID of the requested activities
     * @return All Activities with the given User & Challenge IDs
     */
    public List<ActivityDTO> getActivitiesForUserInChallenge(Long challengeID, Long userID) throws NotFoundException {
        return activityConverter.convertEntityListToDtoList(activityRepository.findActivitiesByChallenge_IDAndMember_ID(challengeID, userID));
    }

    /**
     * Returns all members whose last activity was more than one week ago
     * @return List of all members whose last activity was more than one week ago
     */
    public List<MemberDTO> getAllMembersWhoseLastActivityWasMoreThanOneWeekAgo(){
        return memberConverter.convertEntityListToDtoList(memberRepository.findMembersWhoseLastActivityWasMoreThanOneWeekAgo());
    }

    /**
     * Returns all members whose last activity was more than one week ago
     * @return List of all members whose last activity was more than one week ago
     */
    public List<TeamDTO> getAllTeamsForMember(long memberID){
        List<Team> team = memberRepository.findTeamsByMemberID(memberID);
        return  teamConverter.convertEntityListToDtoList(team);
    }
}
