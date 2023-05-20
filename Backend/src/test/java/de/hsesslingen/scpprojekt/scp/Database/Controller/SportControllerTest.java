package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *  SportController Tests
 *
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@WebMvcTest(SportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SportControllerTest {

    @MockBean
    private SportRepository sportRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    SAML2Service saml2Service;

    /**
     *Test for Successfully creating a Sport
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addSportSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport = new Sport();
        sport.setId(1);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.save(any(Sport.class))).thenReturn(sport);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/sports/")
                .content(new ObjectMapper().writeValueAsString(sport))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(sportRepository).save(any(Sport.class));
    }

    /**
     * Test for InternalServerError
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addSportSomethingWentWrong()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport  = new Sport();

        when(sportRepository.save(any(Sport.class))).thenThrow(HttpServerErrorException.InternalServerError.class);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/sports/")
                .content(new ObjectMapper().writeValueAsString(sport))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

    }
    /**
     *Test for  creating a Sport with not been login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addSportNogLogin()throws Exception{
        Sport sport = new Sport();
        sport.setId(1);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.save(any(Sport.class))).thenReturn(sport);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/sports/")
                .content(new ObjectMapper().writeValueAsString(sport))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     *Test for Successfully searching a Sport
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void getSportByIDSuccess() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sports/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(sportRepository).findById(1L);
    }

    /**
     *Test for searching a non-existing Sport (Not Found)
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getSportByIDNotFound() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sports/4/").accept(MediaType.APPLICATION_JSON);

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
    public void getSportByIDLogout() throws Exception{
        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sports/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     *Test for searching all Sports  with Success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getAllSportSuccess() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        Sport Rad = new Sport();
        Rad.setId(2L);
        Rad.setName("Rad");
        Rad.setFactor(10);
        List<Sport> sportsList = new ArrayList<>();
        sportsList.add(sport); sportsList.add(Rad);

        when(sportRepository.findAll()).thenReturn(sportsList);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sports/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());

        Mockito.verify(sportRepository).findAll();
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getAllSportLogout() throws Exception{
        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        Sport Rad = new Sport();
        Rad.setId(2L);
        Rad.setName("Rad");
        Rad.setFactor(10);
        List<Sport> sportsList = new ArrayList<>();
        sportsList.add(sport); sportsList.add(Rad);

        when(sportRepository.findAll()).thenReturn(sportsList);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/sports/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }
    /**
     *Test for deleting a Sport with Success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void DeleteSportByIdSuccess() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sports/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().is(204))
                .andReturn();

        Mockito.verify(sportRepository).findById(1L);
        Mockito.verify(sportRepository).deleteById(1L);
    }

    /**
     *Test for deleting a non-existing Sport (Not Found)
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void DeleteSportByIdNotFound() throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sports/3/").accept(MediaType.APPLICATION_JSON);

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
    public void DeleteSportByIdLogOut() throws Exception{
        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/sports/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }
    /**
     *Test for updating a sport with success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void UpdateSportSuccess()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        when(sportRepository.save(any(Sport.class))).thenReturn(sport);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sports/1/")
                .content(new ObjectMapper().writeValueAsString(sport))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(sportRepository).findById(1L);
        Mockito.verify(sportRepository).save(sport);
    }
    /**
     *Test for updating a non-existing Sport (Not Found)
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void UpdateSportNotFound()throws Exception{
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        when(sportRepository.save(any(Sport.class))).thenReturn(sport);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sports/4/")
                .content(new ObjectMapper().writeValueAsString(sport))
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
    public void UpdateSportLogOut()throws Exception{
        Sport sport = new Sport();
        sport.setId(1L);
        sport.setName("Laufen");
        sport.setFactor(10);

        when(sportRepository.findById(1L)).thenReturn(Optional.of(sport));
        when(sportRepository.save(any(Sport.class))).thenReturn(sport);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/sports/1/")
                .content(new ObjectMapper().writeValueAsString(sport))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }

}
