package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamMemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests of TeamMemberService
 *
 * @author Tom Nguyen Dinh
 */

@ActiveProfiles("test")
@SpringBootTest
public class TeamMemberServiceTest {
    @MockBean
    MemberService memberService;
    @MockBean
    TeamService teamService;
    @MockBean
    TeamMemberRepository teamMemberRepository;
    @Autowired
    TeamMemberService teamMemberService;
    @Autowired
    TeamMemberConverter teamMemberConverter;
    List<TeamMember> teamMemberList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        teamMemberList = new ArrayList<>();
        Team t1 = new Team();
        t1.setId(1L);
        Team t2 = new Team();
        t1.setId(2L);

        Member m1 = new Member();
        Member m2 = new Member();

        for (long i = 0; i < 5; i++) {
            TeamMember tm = new TeamMember();
            tm.setId(i);
            tm.setTeam(t1);
            tm.setMember(m1);
            teamMemberList.add(tm);
            when(teamMemberRepository.findById(i)).thenReturn(Optional.of(tm));
        }
        for (long i = 5; i < 10; i++) {
            TeamMember tm = new TeamMember();
            tm.setId(i);
            tm.setTeam(t2);
            tm.setMember(m2);
            teamMemberList.add(tm);
            when(teamMemberRepository.findById(i)).thenReturn(Optional.of(tm));
        }
        when(teamMemberRepository.findAll()).thenReturn(teamMemberList);
        when(teamService.get(1L)).thenReturn(t1);
        when(teamService.get(2L)).thenReturn(t2);

        when(teamMemberRepository.save(any(TeamMember.class))).then(AdditionalAnswers.returnsFirstArg());
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<TeamMember> teamMembers = teamMemberConverter.convertDtoListToEntityList(teamMemberService.getAll());

        for(TeamMember tm : teamMembers){
            boolean test = false;
            for (TeamMember tm1 : teamMemberList){
                if (tm1.getId() == tm.getId() ) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }
        assertEquals(teamMemberList.size(), teamMembers.size());
        verify(teamMemberRepository).findAll();
    }

    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(TeamMember tm : teamMemberList){
            assertEquals(teamMemberConverter.convertEntityToDto(tm).getId(), teamMemberService.get(tm.getId()).getId());
            verify(teamMemberRepository).findById(tm.getId());
        }
    }
    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            teamMemberService.get(15L);
        });
    }
    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestSuccess() throws NotFoundException {
        Team t = new Team();
        t.setId(1);
        when(teamService.get(1L)).thenReturn(t);

        Member m = new Member();
        when(memberService.get(0L)).thenReturn(m);

        teamMemberList.get(0).setTeam(t);
        teamMemberList.get(0).setMember(m);

        TeamMemberDTO newTm = teamMemberService.add(teamMemberConverter.convertEntityToDto(teamMemberList.get(0)));

        assertEquals(newTm.getId(), teamMemberList.get(0).getId());
        assertEquals(newTm.getTeamID(), t.getId());
        assertEquals(newTm.getMemberID(), m.getId());

        verify(teamMemberRepository).save(any(TeamMember.class));
        verify(teamService).get(1L);
        verify(memberService).get(0L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
        when(teamService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            teamMemberService.add(teamMemberConverter.convertEntityToDto(teamMemberList.get(0)));
        });
    }

    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess() throws NotFoundException {
        Team t = new Team();
        t.setId(1);
        when(teamService.get(1L)).thenReturn(t);

        Member m = new Member();
        when(memberService.get(0L)).thenReturn(m);

        teamMemberList.get(1).setTeam(t);
        teamMemberList.get(1).setMember(m);

        TeamMemberDTO newTm = teamMemberService.update(0L,
                teamMemberConverter.convertEntityToDto(teamMemberList.get(1)));

        assertEquals(newTm.getId(), teamMemberList.get(0).getId());
        assertEquals(newTm.getTeamID(), t.getId());
        assertEquals(newTm.getMemberID(), m.getId());

        verify(teamMemberRepository).save(any(TeamMember.class));
        verify(teamService).get(1L);
        verify(memberService).get(0L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {
        when(teamService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            teamMemberService.update(0L, teamMemberConverter.convertEntityToDto(teamMemberList.get(0)));
        });
    }

    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        teamMemberService.delete(1L);
        verify(teamMemberRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            teamMemberService.delete(15L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        teamMemberService.deleteAll();
        verify(teamMemberRepository).deleteAll();
    }





}
