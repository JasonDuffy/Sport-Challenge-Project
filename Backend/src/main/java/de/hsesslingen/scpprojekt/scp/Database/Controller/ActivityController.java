package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.Services.ActivityService;
import de.hsesslingen.scpprojekt.scp.Exceptions.ActivityDateException;
import de.hsesslingen.scpprojekt.scp.Exceptions.InactiveChallengeException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
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
 * REST controller for Activity.
 *
 * @author Jason Patrick Duffy
 */
@RestController
@RequestMapping("/activities")
public class ActivityController {
    @Autowired
    SAML2Service saml2Service;

    @Autowired
    ActivityService activityService;

    /**
     * REST API for returning all activities
     *
     * @param request automatically filled by browser
     * @return List of all activities if logged in
     */
    @Operation(summary = "Get all activities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<ActivityDTO>> getActivities(HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(activityService.getAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning all activities
     *
     * @param request automatically filled by browser
     * @return List of all activities if logged in
     */
    @Operation(summary = "update all activities with current TotalDistance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/TotalDistance/", produces = "application/json")
    public ResponseEntity<List<ActivityDTO>> getActivitiesTotalD(HttpServletRequest request) throws NotFoundException {
        if (saml2Service.isLoggedIn(request)){
            activityService.totalDistanceAll();
            return new ResponseEntity<>(activityService.getAll(),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning Activity data of a given ID
     *
     * @param id ID of the Activity that should be returned
     * @param request automatically filled by browser
     * @return Activity data corresponding to the given ID, 404 otherwise
     */
    @Operation(summary = "Get activity by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/", produces = "application/json")
    public ResponseEntity<ActivityDTO> getActivityByID(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(activityService.get(id), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for adding a new Activity
     *
     * @param activity Activity data for the new Activity
     * @param request automatically filled by browser
     * @return A 201 Code and the Activity data if it worked, 500 otherwise
     */
    @Operation(summary = "Add new Activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Activity successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Inactive Challenge for activity", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member or ChallengeSport not found", content = @Content)
    })
    @PostMapping(path = "/", produces = "application/json")
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody ActivityDTO activity, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(activityService.add(activity), HttpStatus.CREATED);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (InactiveChallengeException | ActivityDateException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for updating an Activity
     *
     * @param activity Activity data for the Activity update
     * @param request automatically filled by browser
     * @return A 200 Code and the Activity data if it worked 404 otherwise
     */
    @Operation(summary = "Updates an Activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable("id") long id, @RequestBody ActivityDTO activity, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(activityService.update(id, activity), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for deleting an Activity
     *
     * @param id ID of the Activity that should be deleted
     * @param request automatically filled by browser
     * @return A 200 Code if it worked
     * otherwise if Activity not found 404
     */
    @Operation(summary = "Deletes an Activity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activity successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content)

    })
    @DeleteMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<Void> deleteActivity(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                activityService.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for deleting all Activities
     *
     * @param request automatically filled by browser
     * @return A 200 Code if it worked
     */
    @Operation(summary = "Deletes all Activities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Activities successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAllActivities(HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            activityService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
