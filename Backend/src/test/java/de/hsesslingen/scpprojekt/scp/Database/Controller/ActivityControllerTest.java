package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Tests for Activity Controller REST API
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ActivityRepository activityRepository;

    @MockBean
    ChallengeSportRepository challengeSportRepository;

    @MockBean
    MemberRepository memberRepository;

    /**
     * Test if all activities are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getActivitiesTestSuccess() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);
        Activity a2 = new Activity();
        a2.setId(2);
        List<Activity> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(activityRepository.findAll()).thenReturn(aList);

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

        Mockito.verify(activityRepository).findAll();
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
        Activity a1 = new Activity();
        a1.setId(1);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(a1));

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

        Mockito.verify(activityRepository).findById(1L);
    }

    /**
     * Test if 404 is returned when no activities are found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getActivityByIDTestNotFound() throws Exception {
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
        ChallengeSport cs1 = new ChallengeSport();
        cs1.setId(2L);
        Member m1 = new Member();

        Activity a1 = new Activity();
        a1.setId(1);
        a1.setChallengeSport(cs1);
        a1.setMember(m1);

        when(memberRepository.findById(0L)).thenReturn(Optional.of(m1));
        when(challengeSportRepository.findById(2L)).thenReturn(Optional.of(cs1));
        when(activityRepository.save(any(Activity.class))).thenReturn(a1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/activities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .param("memberID", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

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
        assertEquals(matcher.group(1), "0");
        assertFalse(matcher.find());

        Mockito.verify(challengeSportRepository).findById(2L);
        Mockito.verify(memberRepository).findById(0L);
        Mockito.verify(activityRepository).save(any(Activity.class));
    }

    /**
     * Test if 404 is returned when member is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void createActivityTestNotFound1() throws Exception {
        ChallengeSport cs1 = new ChallengeSport();
        cs1.setId(2L);
        Member m1 = new Member();

        Activity a1 = new Activity();
        a1.setId(1);
        a1.setChallengeSport(cs1);
        a1.setMember(m1);

        when(challengeSportRepository.findById(2L)).thenReturn(Optional.of(cs1));
        when(activityRepository.save(any(Activity.class))).thenReturn(a1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/activities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .param("memberID", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportRepository).findById(2L);
    }

    /**
     * Test if 404 is returned when challenge sport is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void createActivityTestNotFound2() throws Exception {
        ChallengeSport cs1 = new ChallengeSport();
        cs1.setId(2L);
        Member m1 = new Member();

        Activity a1 = new Activity();
        a1.setId(1);
        a1.setChallengeSport(cs1);
        a1.setMember(m1);

        when(memberRepository.findById(0L)).thenReturn(Optional.of(m1));
        when(activityRepository.save(any(Activity.class))).thenReturn(a1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/activities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .param("memberID", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(memberRepository).findById(0L);
    }

    /**
     * Test if 404 is returned when challenge sport and member are not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void createActivityTestNotFound3() throws Exception {
        ChallengeSport cs1 = new ChallengeSport();
        cs1.setId(2L);
        Member m1 = new Member();

        Activity a1 = new Activity();
        a1.setId(1);
        a1.setChallengeSport(cs1);
        a1.setMember(m1);

        when(activityRepository.save(any(Activity.class))).thenReturn(a1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/activities/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .param("memberID", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportRepository).findById(2L);
        Mockito.verify(memberRepository).findById(0L);
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
        Activity a1 = new Activity();
        a1.setId(1);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(a1));
        when(activityRepository.save(any(Activity.class))).thenReturn(a1);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/activities/1/").accept(MediaType.APPLICATION_JSON)
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

        Mockito.verify(activityRepository).findById(1L);
        Mockito.verify(activityRepository).save(any(Activity.class));
    }

    /**
     * Test if 404 is returned when activity is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateActivityTestSuccessNotFound() throws Exception {
        Activity a1 = new Activity();
        a1.setId(1);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(activityRepository).findById(1L);
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
        Activity a1 = new Activity();
        a1.setId(1);

        when(activityRepository.findById(1L)).thenReturn(Optional.of(a1));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(activityRepository).findById(1L);
        Mockito.verify(activityRepository).deleteById(1L);
    }

    /**
     * Test if 404 is returned when activity is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteActivityTestNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/activities/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(activityRepository).findById(1L);
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
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/activities/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(activityRepository).deleteAll();
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
