package de.hsesslingen.scpprojekt.scp.Authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for SAML2.
 *
 * @author Jason Patrick Duffy
 */
@RestController
@RequestMapping(path = "/saml", produces = "application/json")
public class SAML2Controller {

    /**
     * REST API for returning the SAML2 user data
     *
     * @return A SAML2 user object containing the user data on REST API.
     */
    @GetMapping("/user")
    public SAML2User userDataREST() {
        return getCurrentSAMLUser();
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
}