package de.hsesslingen.scpprojekt.scp.Authentication.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2User;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * REST Controller for SAML2.
 *
 * @author Jason Patrick Duffy
 */
@RestController
@RequestMapping(path = "/saml/", produces = "application/json")
public class SAML2Controller {
    @Autowired
    SAML2Service saml2Service;

    /**
     * REST API for returning the SAML2 user data
     *
     * @return A SAML2 user object containing the user data on REST API.
     */
    @Operation(summary = "Get user data provided by SAML2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is logged in.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SAML2User.class))}),
            @ApiResponse(responseCode = "403", description = "User is not logged in.",
                    content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<SAML2User> userDataREST(HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)) {
            return ResponseEntity.ok().body(saml2Service.getCurrentSAMLUser());
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Logs the user into the IDP and returns them to the frontend
     *
     * @return ResponseEntity that redirects the user to the frontend
     */
    @Operation(summary = "Redirect user to Frontend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "308", description = "Redirection successful.",
                    content = @Content)
    })
    @GetMapping("/login/")
    public ResponseEntity<Void> login(){
        saml2Service.loginUser();
        HttpHeaders headers = new HttpHeaders();

        String frontendURL = System.getenv("SCP_Frontend_URL"); // Read address from environment variable
        if (frontendURL == null) // Default to localhost if not set
            frontendURL = "http://localhost:3000";

        headers.setLocation(URI.create(frontendURL));
        return new ResponseEntity<>(headers, HttpStatus.PERMANENT_REDIRECT);
    }
}