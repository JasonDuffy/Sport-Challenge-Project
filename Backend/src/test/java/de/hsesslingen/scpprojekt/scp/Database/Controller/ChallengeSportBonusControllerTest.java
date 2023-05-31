package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportBonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportBonusService;
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

@ActiveProfiles("test")
@WebMvcTest(ChallengeSportBonusController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ChallengeSportBonusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ChallengeSportBonusService challengeSportBonusService;

    @MockBean
    SAML2Service saml2Service;

    /**
     * Test if all ChallengeSportBonus are returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllChallengeSportBonusTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ChallengeSportBonusDTO c1 = new ChallengeSportBonusDTO();
        c1.setId(1);
        ChallengeSportBonusDTO c2 = new ChallengeSportBonusDTO();
        c2.setId(2);
        List<ChallengeSportBonusDTO> cList = new ArrayList<>();
        cList.add(c1); cList.add(c2);

        when(challengeSportBonusService.getAll()).thenReturn(cList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sport-bonuses/").accept(MediaType.APPLICATION_JSON);

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

        Mockito.verify(challengeSportBonusService).getAll();
    }

    /**
     * Test if Unknown User is thrown
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getChallengeSportBonusTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sport-bonuses/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if ChallengeSportBonus is returned correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getChallengeSportBonusByIDTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ChallengeSportBonusDTO c1 = new ChallengeSportBonusDTO();
        c1.setId(1);
        when(challengeSportBonusService.get(1)).thenReturn(c1);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");

        Mockito.verify(challengeSportBonusService).get(1);
    }

    /**
     * Test if 404 is returned when ChallengeSportBonus is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getChallengeSportBonusByIDTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        when(challengeSportBonusService.get(1)).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportBonusService).get(1);
    }

    /**
     * Test if Unknown User is thrown
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getChallengeSportBonusByIDTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if ChallengeSportBonus is created correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addChallengeSportBonusByIDTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ChallengeSportBonusDTO c1 = new ChallengeSportBonusDTO();
        c1.setChallengeSportID(1);
        c1.setBonusID(2);
        when(challengeSportBonusService.add(any(ChallengeSportBonusDTO.class))).thenReturn(c1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/challenge-sport-bonuses/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(c1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        ChallengeSportBonusDTO result = new ObjectMapper().readValue(content, ChallengeSportBonusDTO.class);

        assertEquals(result.getChallengeSportID(), 1L);
        assertEquals(result.getBonusID(), 2L);

        Mockito.verify(challengeSportBonusService).add(any(ChallengeSportBonusDTO.class));
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addChallengeSportBonusByIDTestNotLoggedID() throws Exception {

        ChallengeSportBonusDTO c1 = new ChallengeSportBonusDTO();
        c1.setChallengeSportID(1);
        c1.setBonusID(2);
        when(challengeSportBonusService.add(any(ChallengeSportBonusDTO.class))).thenReturn(c1);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/challenge-sport-bonuses/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(c1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if 404 is returned when ChallengeSportBonus is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addChallengeSportBonusByIDTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ChallengeSportBonusDTO c1 = new ChallengeSportBonusDTO();
        c1.setChallengeSportID(1);
        c1.setBonusID(2);
        when(challengeSportBonusService.add(any(ChallengeSportBonusDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/challenge-sport-bonuses/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(c1));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportBonusService).add(any(ChallengeSportBonusDTO.class));
    }

    /**
     * Test if ChallengeSportBonus is updated correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateChallengeSportBonusTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ChallengeSportBonusDTO c1 = new ChallengeSportBonusDTO();
        c1.setId(1);
        c1.setChallengeSportID(1);
        c1.setBonusID(2);

        ChallengeSportBonusDTO c2 = new ChallengeSportBonusDTO();
        c2.setId(2);
        c2.setChallengeSportID(4);
        c2.setBonusID(5);

        when(challengeSportBonusService.update(any(Long.class), any(ChallengeSportBonusDTO.class))).thenReturn(c2);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(c2));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();
        System.out.println(content);

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());
        Mockito.verify(challengeSportBonusService).update(any(Long.class),any(ChallengeSportBonusDTO.class));
    }

    /**
     * Test if 404 is returned when ChallengeSportBonus is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateChallengeSportBonusTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        ChallengeSportBonusDTO c1 = new ChallengeSportBonusDTO();
        c1.setId(1);
        c1.setChallengeSportID(1);
        c1.setBonusID(2);

        ChallengeSportBonusDTO c2 = new ChallengeSportBonusDTO();
        c2.setId(2);
        c2.setChallengeSportID(4);
        c2.setBonusID(5);

        when(challengeSportBonusService.update(any(Long.class), any(ChallengeSportBonusDTO.class))).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(c2));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportBonusService).update(any(Long.class),any(ChallengeSportBonusDTO.class));
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void updateChallengeSportBonusTestNotLoggedIn() throws Exception {

        ChallengeSportBonusDTO c1 = new ChallengeSportBonusDTO();
        c1.setId(1);
        c1.setChallengeSportID(1);
        c1.setBonusID(2);

        ChallengeSportBonusDTO c2 = new ChallengeSportBonusDTO();
        c2.setId(2);
        c2.setChallengeSportID(4);
        c2.setBonusID(5);

        when(challengeSportBonusService.update(any(Long.class), any(ChallengeSportBonusDTO.class))).thenReturn(c2);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(c2));

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }
    /**
     * Test if ChallengeSportBonus is deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteChallengeSportBonusTestSuccess() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(challengeSportBonusService).delete(1L);
    }

    /**
     * Test if 404 is returned when ChallengeSportBonus is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteChallengeSportBonusTestNotFound() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);
        doThrow(NotFoundException.class).when(challengeSportBonusService).delete(1L);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(challengeSportBonusService).delete(1L);
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteChallengeSportBonusTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/challenge-sport-bonuses/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

}
