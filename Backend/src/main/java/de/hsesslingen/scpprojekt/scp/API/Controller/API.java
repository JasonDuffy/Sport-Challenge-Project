package de.hsesslingen.scpprojekt.scp.API.Controller;

import de.hsesslingen.scpprojekt.scp.API.APIFunctions;
import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for calls that depend on multiple DBs
 *
 * @author Jason Patrick Duffy
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/API")
public class API {
    @Autowired
    APIFunctions functions;

    @Autowired
    ActivityRepository activityRepository;

    /**
     * Get all activities for a given Challenge ID
     *
     * @param request automatically filled by browser
     * @return List of all activities if logged in
     */
    @Operation(summary = "Get all activities for given Challenge ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities for Challenge found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Activity.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "No activities found.", content = @Content)
    })
    @GetMapping(path = "/challengeActivities/", produces = "application/json")
    public ResponseEntity<List<Activity>> getAllActivitiesForChallenge(@RequestParam Long challengeID, HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            List<Activity> challengeActivities = functions.getActivitiesForChallenge(challengeID);

            if(!challengeActivities.isEmpty()){
                return new ResponseEntity<>(challengeActivities, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get all activities for a given User ID
     *
     * @param request automatically filled by browser
     * @return List of all activities if logged in
     */
    @Operation(summary = "Get all activities for given User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities for User found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Activity.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "No activities found.", content = @Content)
    })
    @GetMapping(path = "/userActivities/", produces = "application/json")
    public ResponseEntity<List<Activity>> getAllActivitiesForUser(@RequestParam Long userID, HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            List<Activity> userActivities = functions.getActivitiesForUser(userID);

            if(!userActivities.isEmpty()){
                return new ResponseEntity<>(userActivities, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get distance of challenge without any bonuses applied
     * @param challengeID ID of the challenge to be checked
     * @param request automatically filled by browser
     * @return Distance covered in distance without bonuses
     */
    @Operation(summary = "Get distance of challenge without any bonuses applied")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities for User found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Float.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "500", description = "Not all activities are part of the same challenge.", content = @Content)
    })
    @GetMapping(path = "/rawChallengeDistance/", produces = "application/json")
    public ResponseEntity<Float> getRawDistanceForChallenge(@RequestParam Long challengeID, HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            try{
                List<Activity> activities = functions.getActivitiesForChallenge(challengeID);

                return new ResponseEntity<>(functions.getRawDistanceForActivities(activities), HttpStatus.OK);
            } catch (InvalidActivitiesException e){
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get distance of challenge with bonuses applied
     * @param challengeID ID of the challenge to be checked
     * @param request automatically filled by browser
     * @return Distance covered in distance with bonuses
     */
    @Operation(summary = "Get distance of challenge with bonuses applied")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities for User found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Float.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "500", description = "Not all activities are part of the same challenge.", content = @Content)
    })
    @GetMapping(path = "/challengeDistance/", produces = "application/json")
    public ResponseEntity<Float> getDistanceForChallenge(@RequestParam Long challengeID, HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            try{
                List<Activity> activities = functions.getActivitiesForChallenge(challengeID);

                return new ResponseEntity<>(functions.getDistanceForActivities(activities), HttpStatus.OK);
            } catch (InvalidActivitiesException e){
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get distance of user in challenge with bonuses applied
     * @param challengeID ID of the challenge to be checked
     * @param userID ID of the user to be checked
     * @param request automatically filled by browser
     * @return Distance covered by user in challenge with bonuses
     */
    @Operation(summary = "Get distance of user in challenge with bonuses applied")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Distance successfully calculated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Float.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "500", description = "Not all activities are part of the same challenge.", content = @Content)
    })
    @GetMapping(path = "/challengeDistanceForUser/", produces = "application/json")
    public ResponseEntity<Float> getDistanceForChallengeForUser(@RequestParam Long challengeID, @RequestParam Long userID, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try{
                List<Activity> activities = functions.getActivitiesForUserInChallenge(challengeID, userID);

                return new ResponseEntity<>(functions.getDistanceForActivities(activities), HttpStatus.OK);
            } catch (InvalidActivitiesException e){
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get distance of user in challenge without any bonuses applied
     * @param challengeID ID of the challenge to be checked
     * @param userID ID of the user to be checked
     * @param request automatically filled by browser
     * @return Distance covered by user in challenge without bonuses
     */
    @Operation(summary = "Get distance of user in challenge without bonuses applied")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Distance successfully calculated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Float.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "500", description = "Not all activities are part of the same challenge.", content = @Content)
    })
    @GetMapping(path = "/rawChallengeDistanceForUser/", produces = "application/json")
    public ResponseEntity<Float> getRawDistanceForChallengeForUser(@RequestParam Long challengeID, @RequestParam Long userID, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try{
                List<Activity> activities = functions.getActivitiesForUserInChallenge(challengeID, userID);

                return new ResponseEntity<>(functions.getRawDistanceForActivities(activities), HttpStatus.OK);
            } catch (InvalidActivitiesException e){
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
