package de.hsesslingen.scpprojekt.scp.Authentication;

import de.hsesslingen.scpprojekt.scp.Authentication.Controller.SAML2Controller;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests of the SAML2Controller class
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@WebMvcTest(SAML2Controller.class)
@AutoConfigureMockMvc(addFilters = false)
class SAML2ControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    SAML2Service saml2Service;

    @Autowired
    SAML2Controller saml2Controller;

    /**
     * Tests if the redirection of login works
     */
    @Test
    @WithMockUser
    void testLogin() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/saml/login/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isPermanentRedirect())
                .andExpect(header().string("location", "http://localhost:3000/"))
                .andReturn();

        verify(saml2Service).loginUser();
    }

    /**
     * Tests if user data is returned correctly when logged in
     */
    @Test
    @WithMockUser
    void testUserDataRestLoggedIn() throws Exception {
        when(saml2Service.isLoggedIn(any(HttpServletRequest.class))).thenReturn(true);

        SAML2User user = new SAML2User("max@example.com", "Max Emilian", "Mustermann");
        when(saml2Service.getCurrentSAMLUser()).thenReturn(user);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/saml/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String result = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\"email\":\"(.*@.*\\..*)\",\"firstName\":\"(.*)\",\"lastName\":\"(.*)\"");
        Matcher matcher = pattern.matcher(result);

        matcher.find();
        assertEquals(matcher.group(1), "max@example.com");
        assertEquals(matcher.group(2), "Max Emilian");
        assertEquals(matcher.group(3), "Mustermann");
        assertFalse(matcher.find());
    }

    /**
     * Tests if user is correctly rejected when not logged in
     */
    @Test
    @WithAnonymousUser
    void testUserDataRestLoggedOut() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/saml/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
