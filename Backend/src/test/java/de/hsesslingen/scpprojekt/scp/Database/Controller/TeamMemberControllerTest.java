package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamMemberService;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
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
    @Autowired
    private MockMvc mockMvc;

    /**
     * test for successful adding a Member to a Team
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addMemberToTeamSuccess()throws Exception{
        Member member = new Member();
        member.setId(3);
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

        assertEquals(result.getTeamID(), 2L);
        assertEquals(result.getMemberID(), 3L);

        Mockito.verify(teamMemberService).add(any(TeamMemberDTO.class));
    }

    /**
     * test by adding a Member to a non-existing Team
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addMemberToTeamNotFound()throws Exception{
        Member member = new Member();
        member.setId(3);
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

        Mockito.verify(teamMemberService).add(any(TeamMemberDTO.class));

    }

    /**
     * test if unknown User is thrown
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addMemberToTeamLogOut()throws Exception{
        Member member = new Member();
        member.setId(3);
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
        Member member = new Member();
        member.setId(3);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        TeamMemberDTO teamMember = new TeamMemberDTO();
        teamMember.setId(1);

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
        Member member = new Member();
        member.setId(3);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        TeamMemberDTO teamMember = new TeamMemberDTO();
        teamMember.setId(1);



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


        doThrow(NotFoundException.class).when(teamMemberService).delete(1L);

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
