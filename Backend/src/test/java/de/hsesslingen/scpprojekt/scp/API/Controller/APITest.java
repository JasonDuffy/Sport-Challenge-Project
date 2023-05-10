package de.hsesslingen.scpprojekt.scp.API.Controller;

import de.hsesslingen.scpprojekt.scp.API.Service.APIService;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Tests for API REST
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@WebMvcTest(API.class)
@AutoConfigureMockMvc(addFilters = false)
public class APITest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    APIService functions;

    @MockBean
    ActivityRepository activityRepository;

    /**
     * Test if all activities are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllActivitiesForChallengeTestSuccess() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForChallenge(1L)).thenReturn(aList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeActivities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

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

        Mockito.verify(functions).getActivitiesForChallenge(1L);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getAllActivitiesForChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeActivities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if 404 is correctly returned when no activities are found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllActivitiesForChallengeTestNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeActivities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(functions).getActivitiesForChallenge(1L);
    }

    /**
     * Test if all activities of user are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllActivitiesForUserTestSuccess() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForUser(1L)).thenReturn(aList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/userActivities/").accept(MediaType.APPLICATION_JSON)
                .param("userID", "1");

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

        Mockito.verify(functions).getActivitiesForUser(1L);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getAllActivitiesForUserTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/userActivities/").accept(MediaType.APPLICATION_JSON)
                .param("userID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if 404 is correctly returned when no user activities are found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllActivitiesForUserTestNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/userActivities/").accept(MediaType.APPLICATION_JSON)
                .param("userID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(functions).getActivitiesForUser(1L);
    }

    /**
     * Test if calculated distance is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getRawDistanceForChallengeTestSuccess() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForChallenge(1L)).thenReturn(aList);
        when(functions.getRawDistanceForActivities(aList)).thenReturn(4.0f);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/rawChallengeDistance/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertEquals(content, "4.0");

        Mockito.verify(functions).getActivitiesForChallenge(1L);
        Mockito.verify(functions).getRawDistanceForActivities(aList);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getRawDistanceForChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/rawChallengeDistance/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

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
    public void getRawDistanceForChallengeTestServerError() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForChallenge(1L)).thenReturn(aList);
        when(functions.getRawDistanceForActivities(aList)).thenThrow(InvalidActivitiesException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/rawChallengeDistance/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

        Mockito.verify(functions).getActivitiesForChallenge(1L);
        Mockito.verify(functions).getRawDistanceForActivities(aList);
    }

    /**
     * Test if calculated distance is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getDistanceForChallengeTestSuccess() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForChallenge(1L)).thenReturn(aList);
        when(functions.getDistanceForActivities(aList)).thenReturn(4.0f);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeDistance/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertEquals(content, "4.0");

        Mockito.verify(functions).getActivitiesForChallenge(1L);
        Mockito.verify(functions).getDistanceForActivities(aList);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getDistanceForChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeDistance/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

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
    public void getDistanceForChallengeTestServerError() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForChallenge(1L)).thenReturn(aList);
        when(functions.getDistanceForActivities(aList)).thenThrow(InvalidActivitiesException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeDistance/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

        Mockito.verify(functions).getActivitiesForChallenge(1L);
        Mockito.verify(functions).getDistanceForActivities(aList);
    }

    /**
     * Test if calculated distance is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getDistanceForChallengeForUserTestSuccess() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForUserInChallenge(1L, 1L)).thenReturn(aList);
        when(functions.getDistanceForActivities(aList)).thenReturn(4.0f);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeDistanceForUser/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1")
                .param("userID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertEquals(content, "4.0");

        Mockito.verify(functions).getActivitiesForUserInChallenge(1L, 1L);
        Mockito.verify(functions).getDistanceForActivities(aList);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getDistanceForChallengeForUserTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeDistanceForUser/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1")
                .param("userID", "1");

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
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForUserInChallenge(1L, 1L)).thenReturn(aList);
        when(functions.getDistanceForActivities(aList)).thenThrow(InvalidActivitiesException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/challengeDistanceForUser/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1")
                .param("userID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

        Mockito.verify(functions).getActivitiesForUserInChallenge(1L, 1L);
        Mockito.verify(functions).getDistanceForActivities(aList);
    }

    /**
     * Test if calculated distance is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getRawDistanceForChallengeForUserTestSuccess() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForUserInChallenge(1L, 1L)).thenReturn(aList);
        when(functions.getRawDistanceForActivities(aList)).thenReturn(4.0f);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/rawChallengeDistanceForUser/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1")
                .param("userID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertEquals(content, "4.0");

        Mockito.verify(functions).getActivitiesForUserInChallenge(1L, 1L);
        Mockito.verify(functions).getRawDistanceForActivities(aList);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getRawDistanceForChallengeForUserTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/rawChallengeDistanceForUser/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1")
                .param("userID", "1");

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
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(functions.getActivitiesForUserInChallenge(1L, 1L)).thenReturn(aList);
        when(functions.getRawDistanceForActivities(aList)).thenThrow(InvalidActivitiesException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/API/rawChallengeDistanceForUser/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1")
                .param("userID", "1");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

        Mockito.verify(functions).getActivitiesForUserInChallenge(1L, 1L);
        Mockito.verify(functions).getRawDistanceForActivities(aList);
    }

}
