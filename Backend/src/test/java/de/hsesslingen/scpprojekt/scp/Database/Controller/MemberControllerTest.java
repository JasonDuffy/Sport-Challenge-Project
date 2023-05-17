package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
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
import org.springframework.web.client.HttpServerErrorException;

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
 *  MemberController Tests
 *
 * @author Tom Nguyen Dinh, Jason Patrick Duffy
 */
@ActiveProfiles("test")
@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MemberControllerTest {

    @MockBean
    MemberService memberService;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    SAML2Service saml2Service;
    @MockBean
    MemberConverter memberConverter;

    /**
     *Test for Successfully creating a member
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addMemberSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO member = new MemberDTO();
        member.setUserID(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberService.add(any(MemberDTO.class))).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/members/")
                .content(new ObjectMapper().writeValueAsString(member))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"firstName\":\"(.*)\",\"lastName\":\"(.*)\",\"userID\":(.*),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "Max");
        assertEquals(matcher.group(2), "Mustermann");
        assertEquals(matcher.group(3), "1");
        assertFalse(matcher.find());

        Mockito.verify(memberService).add(any(MemberDTO.class));
    }

    /**
     *Test for  creating a member with not been login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addMemberNoLogin()throws Exception{
        MemberDTO member = new MemberDTO();
        member.setUserID(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberService.add(any(MemberDTO.class))).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/members/")
                .content(new ObjectMapper().writeValueAsString(member))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     *Test for Successfully searching a member
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void getMemberByIDSuccess() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO member = new MemberDTO();
        member.setUserID(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberService.get(1L)).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"firstName\":\"(.*)\",\"lastName\":\"(.*)\",\"userID\":(.*),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "Max");
        assertEquals(matcher.group(2), "Mustermann");
        assertEquals(matcher.group(3), "1");
        assertFalse(matcher.find());

        Mockito.verify(memberService).get(1L);
    }

    /**
     *Test for searching a non-existing member (Not Found)
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getMemberByIDNotFound() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        when(memberService.get(4L)).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/4/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }
    /**
     * Test if unknown user is correctly turned away
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getMemberByIDLogout() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }


    /**
     *Test for deleting a member with Success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteMemberByIdSuccess() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().is(200))
                .andReturn();

        Mockito.verify(memberService).delete(1L);
    }

    /**
     *Test for deleting a non-existing member (Not Found)
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteMemberByIdNotFound() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        doThrow(NotFoundException.class).when(memberService).delete(3L);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/members/3/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }
    /**
     *Test if unknown User is correctly turned away
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteMemberByIdLogOut() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }
    /**
     *Test for updating a member with success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateMemberSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO member = new MemberDTO();
        member.setUserID(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberService.update(any(Long.class), any(MemberDTO.class))).thenReturn(member);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/members/1/")
                .content(new ObjectMapper().writeValueAsString(member))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"firstName\":\"(.*)\",\"lastName\":\"(.*)\",\"userID\":(.*),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "Max");
        assertEquals(matcher.group(2), "Mustermann");
        assertEquals(matcher.group(3), "1");
        assertFalse(matcher.find());

        Mockito.verify(memberService).update(any(Long.class), any(MemberDTO.class));
    }
    /**
     *Test for updating a non-existing member (Not Found)
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void updateMemberNotFound()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO member = new MemberDTO();
        member.setUserID(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberService.update(any(Long.class), any(MemberDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/members/4/")
                .content(new ObjectMapper().writeValueAsString(member))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

    }

    /**
     * Test for turning unknown User away
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void updateMemberLogOut()throws Exception{
        MemberDTO member = new MemberDTO();
        member.setUserID(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        RequestBuilder request = MockMvcRequestBuilders
                .put("/members/1/")
                .content(new ObjectMapper().writeValueAsString(member))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }
}

