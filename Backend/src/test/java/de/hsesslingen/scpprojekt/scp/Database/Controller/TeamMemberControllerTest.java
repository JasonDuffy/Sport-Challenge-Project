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
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamMemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
    TeamMemberService teamMemberService;
    @MockBean
    SAML2Service saml2Service;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    MemberConverter memberConverter;

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

        TeamMemberDTO team = new TeamMemberDTO();
        team.setId(2);

        TeamMemberDTO teamMember = new TeamMemberDTO();
        teamMember.setId(1);
        teamMember.setTeamID(2);
        teamMember.setMemberID(3);


        when(teamMemberService.add(any(TeamMemberDTO.class))).thenReturn(teamMember);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(teamMember));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String content = res.getResponse().getContentAsString();

        TeamMemberDTO result = new ObjectMapper().readValue(content,TeamMemberDTO.class);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(teamMemberService).add(any(TeamMemberDTO.class));
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

        TeamMemberDTO team = new TeamMemberDTO();
        team.setId(2);

        TeamMemberDTO teamMember = new TeamMemberDTO();
        teamMember.setId(1);
        teamMember.setTeamID(2);
        teamMember.setMemberID(3);

        when(teamMemberService.add(any(TeamMemberDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(teamMember));

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

        TeamMemberDTO team = new TeamMemberDTO();
        team.setId(2);

        TeamMemberDTO teamMember = new TeamMemberDTO();
        teamMember.setId(1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(teamMember));

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

        TeamMemberDTO teamMember = new TeamMemberDTO();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(memberConverter.convertDtoToEntity(member));

        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(teamMember));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teamMembers/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(teamMemberService).delete(1L);
    }

    /**
     * Test delete a Member of a team and not been Login
     * @throws Exception Forbidden
     */

    @Test
    @WithAnonymousUser
    public void deleteMemberofTeamLogout()throws Exception{
        MemberDTO member = new MemberDTO();
        member.setUserID(3L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        TeamMemberDTO teamMember = new TeamMemberDTO();
        teamMember.setId(1);
        teamMember.setTeam(team);
        teamMember.setMember(memberConverter.convertDtoToEntity(member));



        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teamMembers/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();


    }

    /**
     * test without corresponding team-member
     * @throws Exception 404 not Found
     */
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

        Mockito.verify(teamMemberService).delete(1L);
    }

    /**
     * Test if all activity are deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteALLTeamMembers() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(teamMemberService).deleteAll();
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteALLTeamMembersNotLogin() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teamMembers/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }


}
