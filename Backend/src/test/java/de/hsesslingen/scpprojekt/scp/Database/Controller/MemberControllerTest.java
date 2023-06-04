package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.SAML2User;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.ActivityService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberService memberService;
    @MockBean
    SAML2Service saml2Service;
    @MockBean
    MemberConverter memberConverter;
    @MockBean
    ActivityConverter activityConverter;
    @MockBean
    ActivityService activityService;
    @MockBean
    ChallengeService challengeService;

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
        member.setCommunication(true);

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

        Pattern pattern = Pattern.compile("\"firstName\":\"(.*)\",\"lastName\":\"(.*)\",\"userID\":(.*),\"imageID\":null,\"communication\":(.*)}");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "Max");
        assertEquals(matcher.group(2), "Mustermann");
        assertEquals(matcher.group(3), "1");
        assertEquals(matcher.group(4), "true");
        assertFalse(matcher.find());

        verify(memberService).add(any(MemberDTO.class));
    }

    /**
     *Test for  creating a member with not been login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addMemberNotLogin()throws Exception{
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
     *Test for already existing member
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addMemberAlreadyExist()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO member = new MemberDTO();
        member.setUserID(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberService.add(any(MemberDTO.class))).thenThrow(AlreadyExistsException.class);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/members/")
                .content(new ObjectMapper().writeValueAsString(member))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
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
        member.setCommunication(true);

        when(memberService.get(1L)).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"firstName\":\"(.*)\",\"lastName\":\"(.*)\",\"userID\":(.*),\"imageID\":null,\"communication\":(.*)}");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "Max");
        assertEquals(matcher.group(2), "Mustermann");
        assertEquals(matcher.group(3), "1");
        assertEquals(matcher.group(4), "true");
        assertFalse(matcher.find());

        verify(memberService).get(1L);
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
                .andExpect(status().isOk())
                .andReturn();

        verify(memberService).delete(1L);
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
        member.setCommunication(false);

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

        Pattern pattern = Pattern.compile("\"firstName\":\"(.*)\",\"lastName\":\"(.*)\",\"userID\":(.*),\"imageID\":null,\"communication\":(.*)}");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "Max");
        assertEquals(matcher.group(2), "Mustermann");
        assertEquals(matcher.group(3), "1");
        assertEquals(matcher.group(4), "false");
        assertFalse(matcher.find());

        verify(memberService).update(any(Long.class), any(MemberDTO.class));
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

     /**
     * Test if all activities of user are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllActivitiesForUserTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(memberService.getActivitiesForUser(1L)).thenReturn(aList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/activities/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());

        verify(memberService).getActivitiesForUser(1L);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getAllActivitiesForUserTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/activities/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }


    /**
     * Test if calculated distance is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getDistanceForChallengeForUserTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(memberService.getActivitiesForUserInChallenge(1L, 1L)).thenReturn(aList);
        when(activityService.getDistanceForActivities(any())).thenReturn(4.0f);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/1/distance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertEquals(content, "4.0");

        verify(memberService).getActivitiesForUserInChallenge(1L, 1L);
        verify(activityService).getDistanceForActivities(any());
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getDistanceForChallengeForUserTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/1/distance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if 500 is correctly returned on exception
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getDistanceForChallengeForUserTestServerError() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(memberService.getActivitiesForUserInChallenge(1L, 1L)).thenReturn(aList);
        when(activityService.getDistanceForActivities(any())).thenThrow(InvalidActivitiesException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/1/distance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

        verify(memberService).getActivitiesForUserInChallenge(1L, 1L);
        verify(activityService).getDistanceForActivities(any());
    }

    /**
     * Test if calculated distance is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getRawDistanceForChallengeForUserTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(memberService.getActivitiesForUserInChallenge(1L, 1L)).thenReturn(aList);
        when(activityService.getRawDistanceForActivities(any())).thenReturn(4.0f);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/1/rawDistance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertEquals(content, "4.0");

        verify(memberService).getActivitiesForUserInChallenge(1L, 1L);
        verify(activityService).getRawDistanceForActivities(any());
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getRawDistanceForChallengeForUserTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/1/rawDistance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if 500 is correctly returned on exception
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getRawDistanceForChallengeForUserTestServerError() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(memberService.getActivitiesForUserInChallenge(1L, 1L)).thenReturn(aList);
        when(activityService.getRawDistanceForActivities(any())).thenThrow(InvalidActivitiesException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/1/rawDistance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

        verify(memberService).getActivitiesForUserInChallenge(1L, 1L);
        verify(activityService).getRawDistanceForActivities(any());
    }

    /**
     * Test if all members are deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteAllMembersTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/members/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        verify(memberService).deleteAll();
    }

    /**
     * Test if an unknown user is thrown
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteAllMembersTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/members/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }

    /**
     *  Test if  Current user is correct
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void GetCurrentUserSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUserID(1);
        memberDTO.setEmail("max@example.com");

        SAML2User user = new SAML2User("max@example.com", "Max Emilian", "Mustermann");
        when(saml2Service.getCurrentSAMLUser()).thenReturn(user);

        when(memberService.getByEmail(user.getEmail())).thenReturn(memberDTO);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/loggedIn/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        verify(memberService).getByEmail(user.getEmail());
    }

    /**
     *  No User found 404 test
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void GetCurrentUserNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUserID(1);
        memberDTO.setEmail("max@example.com");

        SAML2User user = new SAML2User("max@example.com", "Max Emilian", "Mustermann");
        when(saml2Service.getCurrentSAMLUser()).thenReturn(user);

        when(memberService.getByEmail(user.getEmail())).thenThrow(NotFoundException.class);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/loggedIn/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    /**
     *  User not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void GetCurrentUserNotLoggedIn() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/loggedIn/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     *  Test if  Current Challenges for user is correct
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void GetCurrentChallengeSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUserID(1);
        memberDTO.setEmail("max@example.com");

        when(memberService.get(1)).thenReturn(memberDTO);
        when(challengeService.getCurrentChallengeMemberID(1)).thenReturn(anyList());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/current/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        verify(challengeService).getCurrentChallengeMemberID(1);
    }

    /**
     *  Test if  Not Found  is correct
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void GetCurrentChallengeNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUserID(1);
        memberDTO.setEmail("max@example.com");

        when(memberService.get(1)).thenReturn(memberDTO);
        when(challengeService.getCurrentChallengeMemberID(1)).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/current/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
        verify(challengeService).getCurrentChallengeMemberID(1);
    }

    /**
     *  Test if Unknown user is thrown correct
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void GetCurrentChallengeNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/challenges/current/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

}

