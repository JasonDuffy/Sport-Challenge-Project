package de.hsesslingen.scpprojekt.scp.Authentication;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests of the SAML2Controller class
 * @author Jason Patrick Duffy
 */

@ActiveProfiles("test")
@SpringBootTest
public class SAML2ServiceTest {
    @Autowired
    SAML2Service saml2Service;

    @Mock
    private SecurityContext securityContextMock;

    @Mock
    private Authentication authenticationMock;

    @Mock
    private Saml2AuthenticatedPrincipal saml2AuthenticatedPrincipalMock;

    @MockBean
    MemberService memberService;

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
        SAML2User result = new SAML2Service().getCurrentSAMLUser();
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

        assertTrue(saml2Service.isLoggedIn(req));
    }

    /**
     * Tests if isLoggedIn returns false when there is no session
     */
    @Test
    void isLoggedInTestFalseOne(){
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpSession sess = null;
        req.setSession(sess);

        assertFalse(saml2Service.isLoggedIn(req));
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

        assertFalse(saml2Service.isLoggedIn(req));
    }

    /**
     * Tests if loginUser adds logged in user properly
     * @throws AlreadyExistsException Should never be thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    void loginUserSuccess() throws AlreadyExistsException, NotFoundException {
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(saml2AuthenticatedPrincipalMock);
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:1.2.840.113549.1.9.1")).thenReturn("max@example.com");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.42")).thenReturn("Max Emilian");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.4")).thenReturn("Mustermann");

        // Set mock SecurityContext as the current context
        // Makes it so no real authentication is needed
        SecurityContextHolder.setContext(securityContextMock);

        saml2Service.loginUser();

        verify(memberService).add(any(MemberDTO.class));
    }

    /**
     * Tests if AlreadyExistsException is properly caught
     * @throws AlreadyExistsException Should be caught
     * @throws NotFoundException Should never be thrown
     */
    @Test
    void loginUserAlreadyExists() throws AlreadyExistsException, NotFoundException {
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(saml2AuthenticatedPrincipalMock);
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:1.2.840.113549.1.9.1")).thenReturn("max@example.com");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.42")).thenReturn("Max Emilian");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.4")).thenReturn("Mustermann");

        // Set mock SecurityContext as the current context
        // Makes it so no real authentication is needed
        SecurityContextHolder.setContext(securityContextMock);

        when(memberService.add(any(MemberDTO.class))).thenThrow(new AlreadyExistsException("Already Exists"));

        verify(memberService, times(0)).add(any(MemberDTO.class));
    }

    /**
     * Tests is NotFoundException is properly caught
     * @throws AlreadyExistsException Should never be thrown
     * @throws NotFoundException Should be caught
     */
    @Test
    void loginUserNotFound() throws AlreadyExistsException, NotFoundException {
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(saml2AuthenticatedPrincipalMock);
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:1.2.840.113549.1.9.1")).thenReturn("max@example.com");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.42")).thenReturn("Max Emilian");
        when(saml2AuthenticatedPrincipalMock.getFirstAttribute("urn:oid:2.5.4.4")).thenReturn("Mustermann");

        // Set mock SecurityContext as the current context
        // Makes it so no real authentication is needed
        SecurityContextHolder.setContext(securityContextMock);

        when(memberService.add(any(MemberDTO.class))).thenThrow(new NotFoundException("Not Found"));

        verify(memberService, times(0)).add(any(MemberDTO.class));
    }
}
