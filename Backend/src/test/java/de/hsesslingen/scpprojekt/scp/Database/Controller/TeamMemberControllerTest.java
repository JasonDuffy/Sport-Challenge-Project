package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Request;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *  TeamMemberController Tests
 *
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@WebMvcTest(TeamMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TeamMemberControllerTest {

    @MockBean
    TeamRepository teamRepository;
    @MockBean
    MemberRepository memberRepository;
    @MockBean
    TeamMemberRepository teamMemberRepository;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    MemberConverter memberConverter;
    @MockBean
    SAML2Service saml2Service;

    /**
     * test for successful adding a Member to a Team
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addMemberToTeamSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        Team team = new Team();
        team.setId(2);
        team.setName("red Nidhogg");

        TeamMember teamMember = new TeamMember();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(member);

        when(memberRepository.findById(3L)).thenReturn(Optional.of(member));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .param("TeamID", "2")
                .param("MemberID", "3");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(memberRepository).findById(3L);
        Mockito.verify(teamRepository).findById(2L);
        Mockito.verify(teamMemberRepository).save(any(TeamMember.class));
    }

    /**
     * test by adding a Member to a non-existing Team
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addMemberToTeamNotFound1()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        Team team = new Team();
        team.setId(2);
        team.setName("red Nidhogg");

        TeamMember teamMember = new TeamMember();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(member);

        when(memberRepository.findById(3L)).thenReturn(Optional.of(member));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .param("TeamID", "2")
                .param("MemberID", "3");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(memberRepository).findById(3L);

    }

    /**
     * test by adding non-existing Member to Team
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addMemberToTeamNotFound2()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO member = new MemberDTO();
        member.setUserID(3L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        Team team = new Team();
        team.setId(2);
        team.setName("red Nidhogg");

        TeamMember teamMember = new TeamMember();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(memberConverter.convertDtoToEntity(member));

        when(teamRepository.findById(2L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .param("TeamID", "2")
                .param("MemberID", "3");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(teamRepository).findById(2L);

    }

    /**
     * test if unknown User is thrown
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addMemberToTeamLogOut()throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        Team team = new Team();
        team.setId(2);
        team.setName("red Nidhogg");

        TeamMember teamMember = new TeamMember();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(member);

        when(memberRepository.findById(3L)).thenReturn(Optional.of(member));
        when(teamRepository.findById(2L)).thenReturn(Optional.of(team));
        when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .param("TeamID", "2")
                .param("MemberID", "3");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * test for deleting TeamMember
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteMemberofTeamSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO member = new MemberDTO();
        member.setUserID(3L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        Team team = new Team();
        team.setId(2);
        team.setName("red Nidhogg");

        TeamMember teamMember = new TeamMember();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(memberConverter.convertDtoToEntity(member));

        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teamMembers/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(teamMemberRepository).findById(1L);
        Mockito.verify(teamMemberRepository).deleteById(1L);
    }

    @Test
    @WithAnonymousUser
    public void deleteMemberofTeamLogout()throws Exception{
        MemberDTO member = new MemberDTO();
        member.setUserID(3L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        Team team = new Team();
        team.setId(2);
        team.setName("red Nidhogg");

        TeamMember teamMember = new TeamMember();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(memberConverter.convertDtoToEntity(member));



        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teamMembers/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();


    }

    @Test
    @WithMockUser
    public void deleteMemberofTeamNotFound()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO member = new MemberDTO();
        member.setUserID(3L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        Team team = new Team();
        team.setId(2);
        team.setName("red Nidhogg");

        TeamMember teamMember = new TeamMember();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(memberConverter.convertDtoToEntity(member));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teamMembers/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(teamMemberRepository).findById(1L);
    }


}
