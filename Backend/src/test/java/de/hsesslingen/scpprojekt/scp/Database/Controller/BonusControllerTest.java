package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Services.BonusService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import jakarta.servlet.ServletException;
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
 * Tests for Bonus Controller REST API
 * @author Jason Patrick Duffy, Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@WebMvcTest(BonusController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BonusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BonusService bonusService;
    @MockBean
    ChallengeSportService challengeSportService;

    @MockBean
    SAML2Service saml2Service;

    /**
     * Test if all bonuses are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getBonusesTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        BonusDTO b1 = new BonusDTO();
        b1.setId(1);
        BonusDTO b2 = new BonusDTO();
        b2.setId(2);
        List<BonusDTO> bList = new ArrayList<>();
        bList.add(b1); bList.add(b2);

        when(bonusService.getAll()).thenReturn(bList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/bonuses/").accept(MediaType.APPLICATION_JSON);

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

        Mockito.verify(bonusService).getAll();
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getBonusesTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bonuses/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if bonus is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getBonusByIDTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        BonusDTO b1 = new BonusDTO();
        b1.setId(1);

        when(bonusService.get(1L)).thenReturn(b1);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/bonuses/1/").accept(MediaType.APPLICATION_JSON);

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

        Mockito.verify(bonusService).get(1L);
    }

    /**
     * Test if 404 is returned when no bonuses are found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getBonusByIDTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        when(bonusService.get(1L)).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/bonuses/1/").accept(MediaType.APPLICATION_JSON);

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
    public void getBonusByIDTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bonuses/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if bonus is created correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void createBonusTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        BonusDTO b1 = new BonusDTO();
        when(bonusService.add(any(BonusDTO.class), any(long[].class))).thenReturn(b1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/bonuses/?challengesportID=1&challengesportID=2").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(b1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        BonusDTO result = new ObjectMapper().readValue(content, BonusDTO.class);

        Mockito.verify(bonusService).add(any(BonusDTO.class),any(long[].class));
    }

    /**
     * Test if 404 is returned when ChallengeSport is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void createBonusTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        BonusDTO b1 = new BonusDTO();
        b1.setId(1);

        when(bonusService.add(any(BonusDTO.class),any(long[].class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/bonuses/?challengesportID=1&challengesportID=2").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(b1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(bonusService).add(any(BonusDTO.class),any(long[].class));
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void createBonusTestNotLoggedIn() throws Exception {
        BonusDTO b1 = new BonusDTO();
        b1.setId(1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/bonuses/?challengesportID=1&challengesportID=2").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(b1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if bonus is updated correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateBonusTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        BonusDTO b1 = new BonusDTO();
        b1.setId(1);

        when(bonusService.update(any(Long.class), any(BonusDTO.class), any())).thenReturn(b1);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(b1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(bonusService).update(any(Long.class), any(BonusDTO.class), any());
    }

    /**
     * Test if 404 is returned when bonus is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateBonusTestSuccessNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        BonusDTO b1 = new BonusDTO();
        b1.setId(1);

        when(bonusService.update(any(Long.class), any(BonusDTO.class), any())).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(b1));

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(request);
        });

        Mockito.verify(bonusService).update(any(Long.class), any(BonusDTO.class), any());
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void updateBonusTestNotLoggedIn() throws Exception {
        BonusDTO b1 = new BonusDTO();
        b1.setId(1);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .param("challengeSportID", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(b1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if bonus is deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteBonusTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(bonusService).delete(1L);
    }

    /**
     * Test if 404 is returned when bonus is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteBonusTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        doThrow(NotFoundException.class).when(bonusService).delete(1L);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(bonusService).delete(1L);
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteBonusTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if all bonus are deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteAllBonusesTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/bonuses/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(bonusService).deleteAll();
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteAllBonusesTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/bonuses/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void getSportsForBonusTestSuccess() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        Sport sport = new Sport("Laufen",1);
        List<Sport> sports = new ArrayList<>();
        sports.add(sport);
        when(bonusService.getSportsForBonus(1)).thenReturn(sports);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bonuses/1/sports/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"name\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "Laufen");
        assertFalse(matcher.find());

    }
    @Test
    @WithAnonymousUser
    public void getSportsForBonusTestNotLoggedIn() throws Exception{
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bonuses/1/sports/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }

}
