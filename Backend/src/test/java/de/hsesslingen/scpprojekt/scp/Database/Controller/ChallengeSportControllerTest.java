package de.hsesslingen.scpprojekt.scp.Database.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for ChallengeSportController REST API
 *
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@WebMvcTest(ChallengeSportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChallengeSportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ChallengeSportService challengeSportService;
    @MockBean
    private SAML2Service saml2Service;

    /**
     * Test if all ChallengeSport are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getALLChallengeSportTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        ChallengeSportDTO Cs1 = new ChallengeSportDTO();
        Cs1.setId(1);
        ChallengeSportDTO Cs2 = new ChallengeSportDTO();
        Cs2.setId(2);
        List<ChallengeSportDTO> aList = new ArrayList<>();
        aList.add(Cs1); aList.add(Cs2);

        when(challengeSportService.getAll()).thenReturn(aList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sports/").accept(MediaType.APPLICATION_JSON);

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

        Mockito.verify(challengeSportService).getAll();
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getALLChallengeSportTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sports/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }
    /**
     * Test if Challenge-Sport is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getChallengeSportByIDTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        ChallengeSportDTO Cs1 = new ChallengeSportDTO();
        Cs1.setId(1);

        when(challengeSportService.get(1L)).thenReturn(Cs1);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON);

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

        Mockito.verify(challengeSportService).get(1L);
    }
    /**
     * Test if 404 is returned when no activities are found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getChallengeSportByIDTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        when(challengeSportService.get(1L)).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON);

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
    public void getChallengeSportByIDTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if Challenge-Sport is created correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addChallengeSportTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        ChallengeSportDTO Cs1 = new ChallengeSportDTO();
        Cs1.setId(1);
        Cs1.setSportID(4L);
        Cs1.setChallengeID(2L);


        when(challengeSportService.add(any(ChallengeSportDTO.class))).thenReturn(Cs1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/challenge-sports/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(Cs1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        ChallengeSportDTO result = new ObjectMapper().readValue(content, ChallengeSportDTO.class);


        assertEquals(result.getChallengeID(), 2L);
        assertEquals(result.getSportID(), 4L);


        Mockito.verify(challengeSportService).add(any(ChallengeSportDTO.class));
    }

    /**
     * Test if 404 is returned when member is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addChallengeSportTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        ChallengeSportDTO Cs1 = new ChallengeSportDTO();
        Cs1.setId(1);
        Cs1.setChallengeID(2);
        Cs1.setSportID(3);
        when(challengeSportService.add(any(ChallengeSportDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/challenge-sports/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "2")
                .param("sportID", "0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(Cs1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportService).add(any(ChallengeSportDTO.class));
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addChallengeSportNotLoggedIn() throws Exception {
        ChallengeSportDTO Cs1 = new ChallengeSportDTO();
        Cs1.setId(1);
        Cs1.setChallengeID(2);
        Cs1.setSportID(3);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/challenge-sports/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "1")
                .param("sportID", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(Cs1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if Challenge-Sport is updated correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateChallengeSportTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        ChallengeSportDTO Cs1 = new ChallengeSportDTO();
        Cs1.setId(1);

        when(challengeSportService.update(any(Long.class), any(ChallengeSportDTO.class))).thenReturn(Cs1);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "0")
                .param("sportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(Cs1));

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

        Mockito.verify(challengeSportService).update(any(Long.class),any(ChallengeSportDTO.class));
    }

    /**
     * Test if 404 is returned when activity is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateChallengeSportTestSuccessNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        ChallengeSportDTO a1 = new ChallengeSportDTO();
        a1.setId(1);

        when(challengeSportService.update(any(Long.class), any(ChallengeSportDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "0")
                .param("sportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportService).update(any(Long.class), any(ChallengeSportDTO.class));
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void updateChallengeSportTestNotLoggedIn() throws Exception {
        ChallengeSportDTO a1 = new ChallengeSportDTO();
        a1.setId(1);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON)
                .param("challengeID", "0")
                .param("sportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(a1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if Challenge-Sport is deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteChallengeSportTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(challengeSportService).delete(1L);
    }

    /**
     * Test if 404 is returned when activity is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteChallengeSportTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        doThrow(NotFoundException.class).when(challengeSportService).delete(1L);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportService).delete(1L);
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteChallengeSportTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenge-sports/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if all Challenge-Sport are deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteAllChallengeSportsTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenge-sports/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(challengeSportService).deleteAll();
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteAllChallengeSportsTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenge-sports/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }
    /**
     * Test if all ChallengeSport form a Challenge are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getALLChallengeSportForChallengeTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        ChallengeSportDTO Cs1 = new ChallengeSportDTO();
        Cs1.setId(1);
        Cs1.setChallengeID(1);
        ChallengeSportDTO Cs2 = new ChallengeSportDTO();
        Cs2.setId(2);
        Cs2.setChallengeID(1);
        List<ChallengeSportDTO> aList = new ArrayList<>();
        aList.add(Cs1); aList.add(Cs2);

        when(challengeSportService.getAllChallengeSportsOfChallenge(any(long.class))).thenReturn(aList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sports/challenges/1/").accept(MediaType.APPLICATION_JSON);

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

        Mockito.verify(challengeSportService).getAllChallengeSportsOfChallenge(any(long.class));
    }

    /**
     * Test if Unknown user thrown
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getALLChallengeSportForChallengeTestNotLoggedIn() throws Exception {
        ChallengeSportDTO Cs1 = new ChallengeSportDTO();
        Cs1.setId(1);
        Cs1.setChallengeID(1);
        ChallengeSportDTO Cs2 = new ChallengeSportDTO();
        Cs2.setId(2);
        Cs2.setChallengeID(1);
        List<ChallengeSportDTO> aList = new ArrayList<>();
        aList.add(Cs1); aList.add(Cs2);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sports/challenges/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }

}
