package de.hsesslingen.scpprojekt.scp.Authentication.Services;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;

/**
 * Functions concerning SAML2.
 *
 * @author Jason Patrick Duffy
 */
public class SAML2Service {

    /**
     * Prepares SAML2 user object with correct information and returns data as SAML2User object.
     *
     * @return A SAML2 user object containing the user data
     */
    public static SAML2User getCurrentSAMLUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();

        //Save data to strings
        String emailAddress = principal.getFirstAttribute("urn:oid:1.2.840.113549.1.9.1");
        String firstName = principal.getFirstAttribute("urn:oid:2.5.4.42");
        String lastName = principal.getFirstAttribute("urn:oid:2.5.4.4");

        return new SAML2User(emailAddress, firstName, lastName);
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
