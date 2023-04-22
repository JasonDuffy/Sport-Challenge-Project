package de.hsesslingen.scpprojekt.scp.Authentication;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for SAML2.
 * @author Jason Patrick Duffy
 */
@RestController
@RequestMapping(path = "/saml", produces="application/json")
public class SAMLController {

    /**
     * REST API for returning the SAML2 user data
     * @param principal The SAML2 authentication principal. (Keycloak)
     * @return A SAML user object containing the user data.
     */
    @GetMapping("/")
    public SAMLUser userData(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal) {
        String emailAddress = principal.getFirstAttribute("urn:oid:1.2.840.113549.1.9.1");
        String firstName = principal.getFirstAttribute("urn:oid:2.5.4.42");
        String lastName = principal.getFirstAttribute("urn:oid:2.5.4.4");

        SAMLUser thisUser = new SAMLUser(emailAddress, firstName, lastName);

        return thisUser;
    }
}