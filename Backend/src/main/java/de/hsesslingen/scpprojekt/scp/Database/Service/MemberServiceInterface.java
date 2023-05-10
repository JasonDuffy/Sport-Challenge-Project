package de.hsesslingen.scpprojekt.scp.Database.Service;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;

import java.util.List;

public interface MemberServiceInterface {
    /**
     * Returns all members in database
     * @return List of all Bonuses in DB
     */
    public List<Member> getAll();

    /**
     * Returns member with given ID in DB
     * @param memberID ID of desired member
     * @return Member with given ID
     * @throws NotFoundException Member can not be found
     */
    public Member get(Long memberID) throws NotFoundException;

    /**
     * Adds a given member to the DB
     * @param member Member object to be added to DB
     * @return Added member object
     */
    public Member add(Member member) throws  NotFoundException;

    /**
     * Updates a member
     * @param memberID ID of the member to be updated
     * @param member Member object that overwrites the old member
     * @return Updated bonus object
     */
    public Member update(Long memberID, Member member) throws NotFoundException;

    /**
     * Deletes a specific member from the DB
     * @param memberID ID of the bonus to be deleted
     */
    public void delete(Long memberID) throws NotFoundException;

    /**
     * Deletes all members from the DB
     */
    public void deleteAll();
}
