package de.hsesslingen.scpprojekt.scp.Authentication;

import de.hsesslingen.scpprojekt.scp.Authentication.Controller.SAML2Controller;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests of the SAML2Controller class
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@SpringBootTest
class SAML2ControllerTest {

    @Mock
    private SecurityContext securityContextMock;

    @Mock
    private Authentication authenticationMock;

    @Mock
    private Saml2AuthenticatedPrincipal saml2AuthenticatedPrincipalMock;

    /**
     * Tests if the redirection of login works
     */
    @Test
    void testLogin(){
        ResponseEntity<Void> res = new SAML2Controller().login();
        HttpHeaders headers = res.getHeaders();

        assertEquals(headers.getLocation().toString(), "http://localhost:3000/");
        assertEquals(res.getStatusCode(), HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * Tests if user data is returned correctly when logged in
     */
    @Test
    void testUserDataRestLoggedIn(){
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(saml2AuthenticatedPrincipalMock);
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:1.2.840.113549.1.9.1")).thenReturn("max@example.com");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.42")).thenReturn("Max Emilian");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.4")).thenReturn("Mustermann");

        // Set mock SecurityContext as the current context
        // Makes it so no real authentication is needed
        SecurityContextHolder.setContext(securityContextMock);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession sess = new MockHttpSession();
        sess.setAttribute("SPRING_SECURITY_CONTEXT", "SAML2");
        request.setSession(sess);

        SAML2Controller saml2Controller = new SAML2Controller();

        ResponseEntity<SAML2User> result = saml2Controller.userDataREST(request);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
        assertEquals(result.getBody().getEmail(), "max@example.com");
        assertEquals(result.getBody().getFirstName(), "Max Emilian");
        assertEquals(result.getBody().getLastName(), "Mustermann");
    }

    /**
     * Tests if user is correctly rejected when not logged in
     */
    @Test
    void testUserDataRestLoggedOut(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession sess = new MockHttpSession();
        sess.setAttribute("SPRING_SECURITY_CONTEXT", null);
        request.setSession(sess);

        SAML2Controller saml2Controller = new SAML2Controller();

        ResponseEntity<SAML2User> result = saml2Controller.userDataREST(request);
        assertEquals(result.getStatusCode(), HttpStatus.FORBIDDEN);
    }
}
