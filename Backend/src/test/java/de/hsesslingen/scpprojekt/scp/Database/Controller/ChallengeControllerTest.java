package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.ActivityService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the ChallengeController class
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@WebMvcTest(ChallengeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChallengeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChallengeService challengeService;
    @MockBean
    private ImageStorageService imageStorageService;
    @MockBean
    private ChallengeRepository challengeRepository;
    @MockBean
    private ChallengeSportRepository challengeSportRepository;
    @MockBean
    private SportRepository sportRepository;
    @MockBean
    private ActivityConverter activityConverter;
    @MockBean
    private ActivityService activityService;
    @MockBean
    SAML2Service saml2Service;

    @Test
    @WithMockUser
    public void getChallengeByIDSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        long id = 1L;
        float target = 100.60f;
        String name = "Test Challenge";
        String desc = "This challenge is a test.";
        LocalDateTime start = LocalDateTime.of(2023, 5, 8, 10, 0);
        String startAsString = "08.05.2023,10:00";
        LocalDateTime end = LocalDateTime.of(2023, 6, 8, 10, 0);
        String endAsString = "08.06.2023,10:00";
        Image image = null;

        Challenge challenge = new Challenge();
        challenge.setId(id);
        challenge.setName(name);
        challenge.setStartDate(start);
        challenge.setEndDate(end);
        challenge.setDescription(desc);
        challenge.setTargetDistance(target);
        challenge.setImage(image);

        when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(desc))
                .andExpect(jsonPath("$.startDate").value(startAsString))
                .andExpect(jsonPath("$.endDate").value(endAsString))
                .andExpect(jsonPath("$.targetDistance").value(target))
                .andExpect(jsonPath("$.image").value(image))
                .andReturn();
    }

    @Test
    @WithMockUser
    public void getChallengeByIDNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void getChallengeByIDLoggedOut() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void getAllChallengesSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        LocalDateTime start = LocalDateTime.of(2023, 4, 8, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 8, 10, 0);

        Challenge challenge = new Challenge();
        challenge.setId(1L);
        challenge.setStartDate(start);
        challenge.setEndDate(end);

        LocalDateTime start2 = LocalDateTime.of(2022, 4, 8, 10, 0);
        LocalDateTime end2 = LocalDateTime.of(2022, 10, 8, 10, 0);

        Challenge challenge2 = new Challenge();
        challenge2.setId(2L);
        challenge2.setStartDate(start2);
        challenge2.setEndDate(end2);

        List<Challenge> challengeList = new ArrayList<>();
        challengeList.add(challenge);
        challengeList.add(challenge2);

        when(challengeRepository.findAll()).thenReturn(challengeList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/").accept(MediaType.APPLICATION_JSON).param("type", " ");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString(); //Not converted to object as the date format is not compatible with LocalDateTime

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());
    }

    @Test
    @WithMockUser
    public void getCurrentChallengesSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        LocalDateTime start = LocalDateTime.of(2023, 4, 8, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 8, 10, 0);

        Challenge challenge = new Challenge();
        challenge.setId(1L);
        challenge.setStartDate(start);
        challenge.setEndDate(end);

        LocalDateTime start2 = LocalDateTime.of(2022, 4, 8, 10, 0);
        LocalDateTime end2 = LocalDateTime.of(2022, 10, 8, 10, 0);

        Challenge challenge2 = new Challenge();
        challenge2.setId(2L);
        challenge2.setStartDate(start2);
        challenge2.setEndDate(end2);

        List<Challenge> challengeList = new ArrayList<>();
        challengeList.add(challenge);
        challengeList.add(challenge2);

        when(challengeRepository.findAll()).thenReturn(challengeList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/").accept(MediaType.APPLICATION_JSON).param("type", "current");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString(); //Not converted to object as the date format is not compatible with LocalDateTime

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());
    }

    @Test
    @WithMockUser
    public void getPastChallengesSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        LocalDateTime start = LocalDateTime.of(2023, 4, 8, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 8, 10, 0);

        Challenge challenge = new Challenge();
        challenge.setId(1L);
        challenge.setStartDate(start);
        challenge.setEndDate(end);

        LocalDateTime start2 = LocalDateTime.of(2022, 4, 8, 10, 0);
        LocalDateTime end2 = LocalDateTime.of(2022, 10, 8, 10, 0);

        Challenge challenge2 = new Challenge();
        challenge2.setId(2L);
        challenge2.setStartDate(start2);
        challenge2.setEndDate(end2);

        List<Challenge> challengeList = new ArrayList<>();
        challengeList.add(challenge);
        challengeList.add(challenge2);

        when(challengeRepository.findAll()).thenReturn(challengeList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/").accept(MediaType.APPLICATION_JSON).param("type", "past");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString(); //Not converted to object as the date format is not compatible with LocalDateTime

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());
    }

    @Test
    @WithAnonymousUser
    public void getAllChallengesNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/").accept(MediaType.APPLICATION_JSON).param("type", " ");

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /*
    @Test
    @WithMockUser
    public void addChallengeSuccess() throws Exception {
        long id = 1L;
        float target = 100.60f;
        String name = "Test Challenge";
        String desc = "This challenge is a test.";
        LocalDateTime start = null;
        LocalDateTime end = null;
        Image image = new Image();

        Challenge challenge = new Challenge();
        challenge.setId(id);
        challenge.setName(name);
        challenge.setStartDate(start);
        challenge.setEndDate(end);
        challenge.setDescription(desc);
        challenge.setTargetDistance(target);
        challenge.setImage(image);

        Sport sport1 = new Sport("Bicycle", 2.0f);
        Sport sport2 = new Sport("Sprint", 10.0f);
        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport1));
        when(sportRepository.findById(2L)).thenReturn(Optional.of(sport2));

        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());

        RequestBuilder request =
                MockMvcRequestBuilders.multipart("/challenges/")
                        .file(file)
                        .param("sportId", Arrays.toString(new int[]{1, 2}))
                        .param("sportFactor", Arrays.toString(new float[]{5.0f, 15.0f}))
                        .content(new ObjectMapper().writeValueAsString(challenge))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
    }
     */

    @Test
    @WithMockUser
    public void deleteChallengeSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        long id = 1L;
        float target = 100.60f;
        String name = "Test Challenge";
        String desc = "This challenge is a test.";
        LocalDateTime start = LocalDateTime.of(2023, 5, 8, 10, 0);
        String startAsString = "08.05.2023,10:00";
        LocalDateTime end = LocalDateTime.of(2023, 6, 8, 10, 0);
        String endAsString = "08.06.2023,10:00";
        Image image = null;

        Challenge challenge = new Challenge();
        challenge.setId(id);
        challenge.setName(name);
        challenge.setStartDate(start);
        challenge.setEndDate(end);
        challenge.setDescription(desc);
        challenge.setTargetDistance(target);
        challenge.setImage(image);

        when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenges/1/")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void deleteChallengeNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenges/1/")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void deleteChallengeNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenges/1/")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }
    /**
     * Test if all activities are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllActivitiesForChallengeTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(challengeService.getActivitiesForChallenge(1L)).thenReturn(aList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/activities/").accept(MediaType.APPLICATION_JSON);

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

        verify(challengeService).getActivitiesForChallenge(1L);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getAllActivitiesForChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/activities/").accept(MediaType.APPLICATION_JSON);

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
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/activities/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        verify(challengeService).getActivitiesForChallenge(1L);
    }

    /**
     //     * Test if calculated distance is returned correctly
     //     * @throws Exception by mockMvc
     //     */
    @Test
    @WithMockUser
    public void getRawDistanceForChallengeTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(challengeService.getActivitiesForChallenge(1L)).thenReturn(aList);
        when(activityService.getRawDistanceForActivities(any())).thenReturn(4.0f);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/rawDistance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertEquals(content, "4.0");

        verify(challengeService).getActivitiesForChallenge(1L);
        verify(activityService).getRawDistanceForActivities(any());
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getRawDistanceForChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/rawDistance/").accept(MediaType.APPLICATION_JSON);

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
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(challengeService.getActivitiesForChallenge(1L)).thenReturn(aList);
        when(activityService.getRawDistanceForActivities(any())).thenThrow(InvalidActivitiesException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/rawDistance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

        verify(challengeService).getActivitiesForChallenge(1L);
        verify(activityService).getRawDistanceForActivities(any());
    }

    /**
     * Test if calculated distance is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getDistanceForChallengeTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(challengeService.getActivitiesForChallenge(1L)).thenReturn(aList);
        when(activityService.getDistanceForActivities(any())).thenReturn(4.0f);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/distance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();
        assertEquals(content, "4.0");

        verify(challengeService).getActivitiesForChallenge(1L);
        verify(activityService).getDistanceForActivities(any());
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getDistanceForChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/distance/").accept(MediaType.APPLICATION_JSON);

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
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ActivityDTO a1 = new ActivityDTO();
        a1.setId(1);
        ActivityDTO a2 = new ActivityDTO();
        a2.setId(2);
        List<ActivityDTO> aList = new ArrayList<>();
        aList.add(a1); aList.add(a2);

        when(challengeService.getActivitiesForChallenge(1L)).thenReturn(aList);
        when(activityService.getDistanceForActivities(any())).thenThrow(InvalidActivitiesException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/distance/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

        verify(challengeService).getActivitiesForChallenge(1L);
        verify(activityService).getDistanceForActivities(any());
    }

    /**
     * Test if all teams are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllTeamsForChallengeTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        TeamDTO t1 = new TeamDTO();
        t1.setId(1L);
        TeamDTO t2 = new TeamDTO();
        t2.setId(2L);
        List<TeamDTO> tList = new ArrayList<>();
        tList.add(t1); tList.add(t2);

        when(challengeService.getChallengeTeams(1L)).thenReturn(tList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/teams/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"id\":(\\d)");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());

        verify(challengeService).getChallengeTeams(1L);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getAllTeamsForChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/teams/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if not found is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllTeamsForChallengeTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        List<TeamDTO> tList = new ArrayList<>();
        when(challengeService.getChallengeTeams(1L)).thenReturn(tList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/teams/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        verify(challengeService).getChallengeTeams(1L);
    }

    /**
     * Test if all teams are deleted
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteTeamsFromChallengeTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenges/1/teams/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNoContent())
                .andReturn();

        verify(challengeService).deleteChallengeTeams(1L);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteTeamsFromChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenges/1/teams/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if all teams are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getMembersForChallengeTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        MemberDTO m1 = new MemberDTO();
        m1.setUserID(1L);
        MemberDTO m2 = new MemberDTO();
        m2.setUserID(2L);
        List<MemberDTO> mList = new ArrayList<>();
        mList.add(m1); mList.add(m2);

        when(challengeService.getChallengeMembers(1L)).thenReturn(mList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/members/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"userID\":(\\d)");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());

        verify(challengeService).getChallengeMembers(1L);
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getMembersForChallengeTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/members/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if not found is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getMembersForChallengeTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        List<MemberDTO> mList = new ArrayList<>();
        when(challengeService.getChallengeMembers(1L)).thenReturn(mList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenges/1/members/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        verify(challengeService).getChallengeMembers(1L);
    }
}