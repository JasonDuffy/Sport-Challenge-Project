package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ActivityRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
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
import java.util.Optional;

/**
 * REST controller for Activity.
 *
 * @author Jason Patrick Duffy
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/activities")
public class ActivityController {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ChallengeSportRepository challengeSportRepository;

    @Autowired
    MemberRepository memberRepository;

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
                            schema = @Schema(implementation = Activity.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<Activity>> getActivities(HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            List<Activity> activities = activityRepository.findAll();
            return new ResponseEntity<>(activities, HttpStatus.OK);
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
                            schema = @Schema(implementation = Activity.class))}),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/", produces = "application/json")
    public ResponseEntity<Activity> getActivityByID(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Activity> activityData = activityRepository.findById(id);
            if (activityData.isPresent()) {
                return new ResponseEntity<>(activityData.get(), HttpStatus.OK);
            } else {
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
                            schema = @Schema(implementation = Activity.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member or ChallengeSport not found", content = @Content)
    })
    @PostMapping(path = "/", produces = "application/json")
    public ResponseEntity<Activity> createActivity(@RequestParam Long challengeSportID, @RequestParam Long memberID, @RequestBody Activity activity, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<ChallengeSport> challSport = challengeSportRepository.findById(challengeSportID);
            Optional<Member> mem = memberRepository.findById(memberID);

            if (challSport.isPresent() && mem.isPresent()){
                Activity newActivity = activityRepository.save(new Activity(challSport.get(), mem.get(), activity.getDistance(), activity.getDate()));
                return new ResponseEntity<>(newActivity, HttpStatus.CREATED);
            }
            else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
                            schema = @Schema(implementation = Activity.class))}),
            @ApiResponse(responseCode = "404", description = "Activity not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<Activity> updateActivity(@PathVariable("id") long id, @RequestBody Activity activity, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Activity> activityData = activityRepository.findById(id);

            if (activityData.isPresent()) {
                Activity newActivity = activityData.get();
                newActivity.setMember(activity.getMember());
                newActivity.setDate(activity.getDate());
                newActivity.setChallengeSport(activity.getChallengeSport());
                newActivity.setDistance(activity.getDistance());
                return new ResponseEntity<>(activityRepository.save(newActivity), HttpStatus.OK);
            } else {
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
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Activity> activityData = activityRepository.findById(id);
            if (activityData.isPresent()){
                activityRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
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
        if (SAML2Functions.isLoggedIn(request)){
            activityRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
