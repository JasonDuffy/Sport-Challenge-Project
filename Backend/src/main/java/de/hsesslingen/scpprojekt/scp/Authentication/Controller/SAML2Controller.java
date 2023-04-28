package de.hsesslingen.scpprojekt.scp.Authentication.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.net.URI;

/**
 * REST Controller for SAML2.
 *
 * @author Jason Patrick Duffy
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping(path = "/saml/", produces = "application/json")
public class SAML2Controller {

    /**
     * REST API for returning the SAML2 user data
     *
     * @return A SAML2 user object containing the user data on REST API.
     */
    @GetMapping("/user/")
    public ResponseEntity<SAML2User> userDataREST(HttpServletRequest request){
        if (isLoggedIn(request)) {
            return ResponseEntity.ok().body(getCurrentSAMLUser());
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Logs the user into the IDP and returns them to the frontend
     *
     * @return ResponsEntity that redirects the user to the frontend
     */
    @GetMapping("/login/")
    public ResponseEntity<Void> login(){
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:3000/"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    /**
     * Prepares SAML2 user object with correct information and returns data as SAML2User object.
     *
     * @return A SAML2 user object containing the user data
     */
    public SAML2User getCurrentSAMLUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();

        //Save data to strings
        String emailAddress = principal.getFirstAttribute("urn:oid:1.2.840.113549.1.9.1");
        String firstName = principal.getFirstAttribute("urn:oid:2.5.4.42");
        String lastName = principal.getFirstAttribute("urn:oid:2.5.4.4");

        SAML2User thisUser = new SAML2User(emailAddress, firstName, lastName);

        return thisUser;
    }

    /**
     * Returns if the user's session is valid
     * Has to be called from HTTP request!
     *
     * @param request, automatically filled by browser
     * @return True/false depending on the user's login state
     */
    public static boolean isLoggedIn(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
            return true;
        } else {
            return false;
        }
    }
}