package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Services.ActivityService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for Member.
 *
 * @author Mason Schönherr, Jason Patrick Duffy, Robin Hackh, Tom Nguyen Dinh
 */
@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    SAML2Service saml2Service;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberConverter memberConverter;
    @Autowired
    ActivityConverter activityConverter;
    @Autowired
    ActivityService activityService;
    @Autowired
    ChallengeService challengeService;

    /**
     * REST API for returning data of all Members
     *
     * @param request automatically filled by browser
     * @return All Member data
     */
    @Operation(summary = "Get all members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<MemberDTO>> getAllMembers(HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            return new ResponseEntity<>(memberService.getAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning Member data of a given ID
     *
     * @param id      id of the Member that should be returned
     * @param request automatically filled by browser
     * @return Member data corresponding to the given ID 404 otherwise
     */
    @Operation(summary = "Get member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<MemberDTO> getMemberByID(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            try {
                return new ResponseEntity<>(memberService.get(id), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for adding a new Member
     *
     * @param member  Member data for the new Member
     * @param request automatically filled by browser
     * @return A 201 Code and the Member data if it worked 500 otherwise
     */
    @Operation(summary = "Adds the new member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member successfully added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "400", description = "User already exists or image not found", content = @Content)
    })
    @PostMapping(path = "/", produces = "application/json")
    public ResponseEntity<MemberDTO> createMember(@RequestBody MemberDTO member, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            try {
                return new ResponseEntity<>(memberService.add(member), HttpStatus.CREATED);
            } catch (AlreadyExistsException | NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for updating a new Member
     *
     * @param id      ID of the Member that should be updated
     * @param member  Member data for the Member update
     * @param request automatically filled by browser
     * @return A 200 Code and the Member data if it worked 404 otherwise
     */
    @Operation(summary = "Updates a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable("id") long id, @RequestBody MemberDTO member, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            try {
                return new ResponseEntity<>(memberService.update(id, member), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for deleting a Member
     *
     * @param id      ID of the Member that should be deleted
     * @param request automatically filled by browser
     * @return A 200 Code and the Member data if it worked
     * otherwise if member not found 404
     */
    @Operation(summary = "Deletes a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)

    })
    @DeleteMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<HttpStatus> deleteMember(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            try {
                memberService.delete(id);
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
     * REST API for deleting all Members
     *
     * @param request automatically filled by browser
     * @return A 200 Code and the Member data if it worked 500 otherwise
     */
    @Operation(summary = "Deletes all members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All members successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong deleting all members", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<HttpStatus> deleteAllMembers(HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            memberService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning Member data of logged in user
     *
     * @param request automatically filled by browser
     * @return Member data corresponding to the user, 404 otherwise
     */
    @Operation(summary = "Get member currently logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/loggedIn/", produces = "application/json")
    public ResponseEntity<MemberDTO> getCurrentMember(HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            try {
                return new ResponseEntity<>(memberService.getByEmail(saml2Service.getCurrentSAMLUser().getEmail()), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get all activities for a given User ID
     *
     * @param userID  The user ID for the activities
     * @param request automatically filled by browser
     * @return List of all activities if logged in
     */
    @Operation(summary = "Get all activities for given User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities for User found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/activities/", produces = "application/json")
    public ResponseEntity<List<ActivityDTO>> getAllActivitiesForUser(@PathVariable("id") long userID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            List<ActivityDTO> userActivities = memberService.getActivitiesForUser(userID);
            return new ResponseEntity<>(userActivities, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get distance of user in challenge with bonuses applied
     *
     * @param challengeID ID of the challenge to be checked
     * @param userID      ID of the user to be checked
     * @param request     automatically filled by browser
     * @return Distance covered by user in challenge with bonuses
     */
    @Operation(summary = "Get distance of user in challenge with bonuses applied")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Distance successfully calculated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Float.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "500", description = "Not all activities are part of the same challenge.", content = @Content)
    })
    @GetMapping(path = "/{id}/challenges/{chID}/distance/", produces = "application/json")
    public ResponseEntity<Float> getDistanceForChallengeForUser(@PathVariable("chID") long challengeID, @PathVariable("id") long userID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            try {
                List<Activity> activities = activityConverter.convertDtoListToEntityList(memberService.getActivitiesForUserInChallenge(challengeID, userID));

                return new ResponseEntity<>(activityService.getDistanceForActivities(activities), HttpStatus.OK);
            } catch (InvalidActivitiesException | NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get distance of user in challenge without any bonuses applied
     *
     * @param challengeID ID of the challenge to be checked
     * @param userID      ID of the user to be checked
     * @param request     automatically filled by browser
     * @return Distance covered by user in challenge without bonuses
     */
    @Operation(summary = "Get distance of user in challenge without bonuses applied")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Distance successfully calculated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Float.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "500", description = "Not all activities are part of the same challenge.", content = @Content)
    })
    @GetMapping(path = "/{id}/challenges/{chID}/rawDistance/", produces = "application/json")
    public ResponseEntity<Float> getRawDistanceForChallengeForUser(@PathVariable("chID") long challengeID, @PathVariable("id") long userID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            try {
                List<Activity> activities = activityConverter.convertDtoListToEntityList(memberService.getActivitiesForUserInChallenge(challengeID, userID));

                return new ResponseEntity<>(activityService.getRawDistanceForActivities(activities), HttpStatus.OK);
            } catch (InvalidActivitiesException | NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning Team data of a given memberID
     *
     * @param memberID id of the Member
     * @param request automatically filled by browser
     * @return Team data corresponding to the given memberID 404 otherwise
     */
    @Operation(summary = "Get teams by memberID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/teams/", produces = "application/json")
    public ResponseEntity <List<TeamDTO>> getTeamByMemberID(@PathVariable("id") long memberID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(memberService.getAllTeamsForMember(memberID), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    
     /** REST API for returning Member data of a given teamID
     *
     * @param teamID ID of the Team the members should be part of
     * @param request automatically filled by browser
     * @return Member data corresponding to the given teamID 404 otherwise
     */
    @Operation(summary = "Get members by teamID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "search successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/teams/{id}/", produces = "application/json")
    public ResponseEntity<List<MemberDTO>> getMemberByTeamID(@PathVariable("id") long teamID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            return new ResponseEntity<>(memberService.getAllMembersByTeamID(teamID), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest APi for getting all current challenges for Member
     *
     * @param memberID ID of Member
     * @param request automatically filled by browser
     * @return List of current challenges
     */
    @Operation(summary = "Get all current Challenges for a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful.",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChallengeDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "No current challenges for this user", content = @Content)
    })
    @GetMapping(path = "/{id}/challenges/current/", produces = "application/json")
    public ResponseEntity<List<ChallengeDTO>> getCurrentChallengesByMemberDate(@PathVariable("id") long memberID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            List<ChallengeDTO> challengeList = new ArrayList<>();
            try {
                challengeList = challengeService.getCurrentChallengeMemberID(memberID);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(challengeList, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
