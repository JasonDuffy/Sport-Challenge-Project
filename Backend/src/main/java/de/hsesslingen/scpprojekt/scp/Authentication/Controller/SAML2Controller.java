package de.hsesslingen.scpprojekt.scp.Authentication.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Authentication.SAML2User;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        if (SAML2Functions.isLoggedIn(request)) {
            return ResponseEntity.ok().body(SAML2Functions.getCurrentSAMLUser());
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Logs the user into the IDP and returns them to the frontend
     *
     * @return ResponsEntity that redirects the user to the frontend
     */
    @Hidden // Hidden as it should not be used in an API request
    @Operation(summary = "Redirect user to Frontend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "301", description = "Redirection successful.",
                    content = @Content)
    })
    @GetMapping("/login/")
    public ResponseEntity<Void> login(){
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:3000/"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}