package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Services.ActivityService;
import de.hsesslingen.scpprojekt.scp.Exceptions.InactiveChallengeException;
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


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Tests for Activity Controller REST API
 * @author Jason Patrick Duffy, Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ActivityService activityService;

    @MockBean
    SAML2Service saml2Service;

    /**
     * Test if all activities are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getActivitiesTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(activityService.getAll()).thenReturn(aList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/activities/").accept(MediaType.APPLICATION_JSON);

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

        Mockito.verify(activityService).getAll();
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getActivitiesTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/activities/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if activity is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getActivityByIDTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);

        when(activityService.get(1L)).thenReturn(a1);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/activities/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(activityService).get(1L);
    }

    /**
     * Test if 404 is returned when no activities are found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getActivityByIDTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        when(activityService.get(1L)).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/activities/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getActivityByIDTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/activities/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if activity is created correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void createActivityTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setChallengeSportID(2L);
        a1.setMemberID(0L);

        when(activityService.add(any(ActivityDTO.class))).thenReturn(a1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/activities/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        ActivityDTO result = new ObjectMapper().readValue(content, ActivityDTO.class);

        assertEquals(result.getChallengeSportID(), 2L);
        assertEquals(result.getMemberID(), 0L);

        Mockito.verify(activityService).add(any(ActivityDTO.class));
    }

    /**
     * Test if 400 is returned when challenge is no longer active
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void createActivityTesTBadRequest() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        a1.setChallengeSportID(2L);
        a1.setMemberID(0L);

        when(activityService.add(any(ActivityDTO.class))).thenThrow(InactiveChallengeException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/activities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .param("memberID", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn();

        Mockito.verify(activityService).add(any(ActivityDTO.class));
    }

    @Test
    @WithMockUser
    public void createActivityTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        a1.setChallengeSportID(2L);
        a1.setMemberID(0L);

        when(activityService.add(any(ActivityDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/activities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .param("memberID", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(activityService).add(any(ActivityDTO.class));
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void createActivityTestNotLoggedIn() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/activities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "1")
                .param("memberID", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if activity is updated correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateActivityTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);

        when(activityService.update(any(Long.class), any(ActivityDTO.class))).thenReturn(a1);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .param("memberID", "0")
                .param("challengeSportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();
        System.out.println(content);

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(activityService).update(any(Long.class),any(ActivityDTO.class));
    }

    /**
     * Test if 404 is returned when activity is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateActivityTestSuccessNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);

        when(activityService.update(any(Long.class), any(ActivityDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .param("memberID", "0")
                .param("challengeSportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(activityService).update(any(Long.class), any(ActivityDTO.class));
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void updateActivityTestNotLoggedIn() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .param("memberID", "0")
                .param("challengeSportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if activity is deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteActivityTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(activityService).delete(1L);
    }

    /**
     * Test if 404 is returned when activity is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteActivityTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        doThrow(NotFoundException.class).when(activityService).delete(1L);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(activityService).delete(1L);
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteActivityTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if all activity are deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteAllActivitiesTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/activities/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(activityService).deleteAll();
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteAllActivitiesTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/activities/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

}
