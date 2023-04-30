package de.hsesslingen.scpprojekt.scp.Authentication;

import de.hsesslingen.scpprojekt.scp.Authentication.Controller.SAML2Controller;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests of the SAML2Controller class
 * @author Jason Patrick Duffy
 */

@ActiveProfiles("test")
@SpringBootTest
public class SAML2FunctionsTest {
    @Mock
    private SecurityContext securityContextMock;

    @Mock
    private Authentication authenticationMock;

    @Mock
    private Saml2AuthenticatedPrincipal saml2AuthenticatedPrincipalMock;

    /**
     * Tests the currentSAMLUser class for valid returns
     */
    @Test
    void testCurrentSAMLUser() {
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(saml2AuthenticatedPrincipalMock);
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:1.2.840.113549.1.9.1")).thenReturn("max@example.com");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.42")).thenReturn("Max Emilian");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.4")).thenReturn("Mustermann");

        // Set mock SecurityContext as the current context
        // Makes it so no real authentication is needed
        SecurityContextHolder.setContext(securityContextMock);

        // Call getCurrentSAMLUser() method and verify that the returned SAML2User object has the correct data
        SAML2User result = new SAML2Functions().getCurrentSAMLUser();
        assertEquals("max@example.com", result.getEmail());
        assertEquals("Max Emilian", result.getFirstName());
        assertEquals("Mustermann", result.getLastName());

        // Verify that the mock objects were called correctly
        verify(securityContextMock).getAuthentication();
        verify(authenticationMock).getPrincipal();
        verify(saml2AuthenticatedPrincipalMock).getFirstAttribute("urn:oid:1.2.840.113549.1.9.1");
        verify(saml2AuthenticatedPrincipalMock).getFirstAttribute("urn:oid:2.5.4.42");
        verify(saml2AuthenticatedPrincipalMock).getFirstAttribute("urn:oid:2.5.4.4");
    }

    /**
     * Tests if isLoggedIn returns true when logged in
     */
    @Test
    void isLoggedInTestTrue(){
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession sess = new MockHttpSession();
        sess.setAttribute("SPRING_SECURITY_CONTEXT", "SAML2");
        req.setSession(sess);

        assertTrue(SAML2Functions.isLoggedIn(req));
    }

    /**
     * Tests if isLoggedIn returns false when there is no session
     */
    @Test
    void isLoggedInTestFalseOne(){
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession sess = null;
        req.setSession(sess);

        assertFalse(SAML2Functions.isLoggedIn(req));
    }

    /**
     * Tests if isLoggedIn returns false when user is not logged in
     */
    @Test
    void isLoggedInTestFalseTwo(){
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession sess = new MockHttpSession();
        sess.setAttribute("SPRING_SECURITY_CONTEXT", null);
        req.setSession(sess);

        assertFalse(SAML2Functions.isLoggedIn(req));
    }
}
