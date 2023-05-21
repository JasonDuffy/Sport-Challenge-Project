package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests of Member Service
 *
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@SpringBootTest
public class MemberServiceTest {
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    ImageStorageService imageStorageService;
    @MockBean
    Filler filler; //Mock Filler because its entries cause exceptions
    @Autowired
    MemberConverter memberConverter;
    @Autowired
    MemberService memberService;

    List<Member> memberList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        memberList = new ArrayList<>();

        for (long i = 0; i < 10; i++){
            Member m = new Member();
            m.setId((long)i);
            m.setEmail("test" + i + "@example.com");
            memberList.add(m);

            when(memberRepository.findMemberByEmail("test" + i + "@example.com")).thenReturn(m);
            when(memberRepository.findById((long)i)).thenReturn(Optional.of(m));
        }

        when(memberRepository.findAll()).thenReturn(memberList);

        when(memberRepository.save(any(Member.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given member class
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<Member> members = memberConverter.convertDtoListToEntityList(memberService.getAll());

        for(Member m : members){
            boolean test = false;
            for (Member m1: memberList){
                if (m1.getId().equals(m.getId())) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }

        assertEquals(members.size(), memberList.size());

        verify(memberRepository).findAll();
    }

    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(Member m: memberList){
            assertEquals(memberConverter.convertEntityToDto(m).getUserID(), memberService.getDTO(m.getId()).getUserID());
            verify(memberRepository).findById(m.getId());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            memberService.get(15L);
        });
    }

    /**
     * Test if getByEmail works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getByEmailSuccess() throws NotFoundException {
        for(Member m: memberList){
            assertEquals(memberConverter.convertEntityToDto(m).getEmail(), memberService.getByEmail(m.getEmail()).getEmail());
            verify(memberRepository).findMemberByEmail(m.getEmail());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getByEmailFail(){
        assertThrows(NotFoundException.class, () -> {
            memberService.getByEmail("test20@example.com");
        });
    }

    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown
     * @throws AlreadyExistsException Should never be thrown
     */
    @Test
    public void addTestSuccess() throws NotFoundException, AlreadyExistsException {
        MemberDTO newMember = memberService.add(memberConverter.convertEntityToDto(memberList.get(0)));

        assertEquals(newMember.getEmail(), memberList.get(0).getEmail());

        verify(memberRepository).save(any(Member.class));
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void addTestFail() {
        when(memberRepository.existsMemberByEmail(memberList.get(1).getEmail())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> {
            memberService.add(memberConverter.convertEntityToDto(memberList.get(1)));
        });
    }

    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess() throws NotFoundException {
        MemberDTO oldMember = memberConverter.convertEntityToDto(memberList.get(2));
        MemberDTO newMember = memberService.update(1L, oldMember);

        assertEquals(1L, newMember.getUserID());
        assertEquals(oldMember.getEmail(), newMember.getEmail());
        assertNotEquals(oldMember.getUserID(), newMember.getUserID());
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> {
            memberService.update(20L, memberConverter.convertEntityToDto(memberList.get(0)));
        });
    }

    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        memberService.delete(1L);
        verify(memberRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            memberService.delete(15L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        memberService.deleteAll();
        verify(memberRepository).deleteAll();
    }
}
