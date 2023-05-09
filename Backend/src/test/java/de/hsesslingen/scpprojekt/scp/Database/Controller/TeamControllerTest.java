package de.hsesslingen.scpprojekt.scp.Database.Controller;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import de.hsesslingen.scpprojekt.scp.Database.Service.ImageStorageService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(TeamController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TeamControllerTest {
    @MockBean
    private TeamRepository teamRepository;
    @MockBean
    private ChallengeRepository challengeRepository;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ImageStorageService imageStorageService;

    @Test
    @WithMockUser
    public void getTeamByIDSuccess() throws Exception {
        Team team = new Team();
        team.setId(1);

        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));

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

        Mockito.verify(teamRepository).findById(1L);
    }
    @Test
    @WithMockUser
    public void getTeamByIDNotFound() throws Exception{
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/teams/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andReturn();
    }
    @Test
    @WithAnonymousUser
    public void getTeamByIDLoggedOut() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/teams/1/").accept(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void getALLTeamsSuccess() throws Exception{
        Team team1 = new Team();
        Team team2 = new Team();
        team1.setId(1);
        team2.setId(2);
        List<Team> teamList = new ArrayList<>();
        teamList.add(team1);
        teamList.add(team2);

        when(teamRepository.findAll()).thenReturn(teamList);

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

        Mockito.verify(teamRepository).findAll();
    }
    @Test
    @WithAnonymousUser
    public void getALLTeamsLogOut() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders
                .get("/teams/").accept(MediaType.APPLICATION_JSON);
        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }
    @Test
    @WithMockUser
    public void deleteATeamSuccess()throws Exception{
        Team team1 = new Team();
        team1.setId(1);
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team1));
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teams/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        Mockito.verify(teamRepository).findById(1L);
        Mockito.verify(teamRepository).deleteById(1L);
    }

    @Test
    @WithMockUser
    public void deleteATeamNotFound()throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teams/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

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

    @Test
    @WithMockUser
    public void deleteALLTeamSuccess()throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/teams/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        Mockito.verify(teamRepository).deleteAll();
    }

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

}
