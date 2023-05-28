package de.hsesslingen.scpprojekt.scp.Database.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for the TeamController
 *
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TeamControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    TeamService teamService;
    @MockBean
    ImageStorageService imageStorageService;
    @MockBean
    SAML2Service saml2Service;

    /**
     * Test for get team by ID SUCCESS
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void getTeamByIDSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        TeamDTO team = new TeamDTO();
        team.setId(1);

        when(teamService.get(1L)).thenReturn(team);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/teams/1/").accept(MediaType.APPLICATION_JSON);

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

        Mockito.verify(teamService).get(1L);
    }

    /**
     * Test for get team by ID not found
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void getTeamByIDNotFound() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        when(teamService.get(1L)).thenThrow(NotFoundException.class);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/teams/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    /**
     * Test for get team by ID not login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getTeamByIDLoggedOut() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/teams/1/").accept(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test for getting all teams Success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getALLTeamsSuccess() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        TeamDTO team1 = new TeamDTO();
        TeamDTO team2 = new TeamDTO();
        team1.setId(1);
        team2.setId(2);
        List<TeamDTO> teamList = new ArrayList<>();
        teamList.add(team1);
        teamList.add(team2);

        when(teamService.getAll()).thenReturn(teamList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/teams/").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);


        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());

        Mockito.verify(teamService).getAll();
    }

    /**
     * Test for getting all teams not login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getALLTeamsLogOut() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders
                .get("/teams/").accept(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test for deleting a team success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteATeamSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teams/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(teamService).delete(1L);
    }

    /**
     * Test for deleting a team not found
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteATeamNotFound()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        doThrow(NotFoundException.class).when(teamService).delete(1L);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teams/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    /**
     * Test for deleting a team not login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteATeamLogOut()throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teams/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test for deleting all team success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteALLTeamSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teams/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        Mockito.verify(teamService).deleteAll();
    }

    /**
     * Test for deleting all team not found
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteALLTeamLogOut()throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teams/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test for creating a team  Success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addTeamSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Challenge challenge = new Challenge();
        challenge.setName("Annas");
        Image image = new Image();
        challenge.setId(2L);

        Team team = new Team();
        team.setId(1L);
        team.setName("Hansen");
        team.setChallenge(challenge);

        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json", "{\"name\": \"Hansen\"}".getBytes());


        RequestBuilder request =
                MockMvcRequestBuilders.multipart("/teams/")
                        .file(file)
                        .file(jsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON);

       mockMvc.perform(request)
               .andExpect(status().isCreated())
               .andReturn();


    }
    /**
     * Test for creating a team not found
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void addTeamNotFound()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        TeamDTO team = new TeamDTO();
        team.setId(1L);

        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json", "{\"name\": \"Hansen\" }".getBytes());

        when(teamService.add(any(MockMultipartFile.class),any(TeamDTO.class))).thenThrow(NotFoundException.class);
        RequestBuilder request =
                MockMvcRequestBuilders.multipart("/teams/")
                        .file(file)
                        .file(jsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    /**
     * Test for creating a team not login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addTeamLogOut()throws Exception{
        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json", "{\"name\": \"Hansen\"}".getBytes());


        RequestBuilder request =
                MockMvcRequestBuilders.multipart("/teams/")
                        .file(file)
                        .file(jsonFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();


    }

    /**
     * Test for updating a team success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateTeamSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Team team = new Team();
        team.setId(1);

        when(teamService.update(any(Long.class),any(Long.class),any(TeamDTO.class))).thenReturn(any(TeamDTO.class));

        RequestBuilder request = MockMvcRequestBuilders
                .put("/teams/1/").accept(MediaType.APPLICATION_JSON)
                .param("imageID" ,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(team));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(teamService).update(any(Long.class),any(Long.class), any(TeamDTO.class));

    }

    /**
     * Test for updating a team not found
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void updateTeamNotFound()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Team team = new Team();
        team.setId(1);

        when(teamService.get(1L)).thenThrow(NotFoundException.class);
        when(teamService.update(any(Long.class),any(Long.class),any(TeamDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/teams/1/").accept(MediaType.APPLICATION_JSON)
                .param("imageID" ,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(team));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
        Mockito.verify(teamService).update(any(Long.class),any(Long.class), any(TeamDTO.class));

    }

    /**
     * Test for updating a team not login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void updateTeamLogOut()throws Exception{
        Team team = new Team();
        team.setId(1);

        when(teamService.update(any(Long.class),any(Long.class),any(TeamDTO.class))).thenReturn(any(TeamDTO.class));

        RequestBuilder request = MockMvcRequestBuilders
                .put("/teams/1/").accept(MediaType.APPLICATION_JSON)
                .param("imageID" ,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(team));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

}
