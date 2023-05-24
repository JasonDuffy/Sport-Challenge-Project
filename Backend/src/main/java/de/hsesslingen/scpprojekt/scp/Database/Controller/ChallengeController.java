package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ActivityConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.ActivityService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for Challenge.
 *
 * @author Robin Hackh, Jason Patrick Duffy, Tom Nguyen Dinh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    @Autowired
    private SAML2Service saml2Service;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private ActivityConverter activityConverter;
    @Autowired
    private ActivityService activityService;

    /**
     * REST API for returning Challenge data of a given ID
     *
     * @param id ID of the Challenge that should be returned
     * @param request automatically filled by browser
     * @return Challenge data corresponding to the given ID 404 otherwise
     */
    @Operation(summary = "Get challenge by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/" , produces = "application/json")
    public ResponseEntity<ChallengeDTO> getChallengeById(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(challengeService.getDTO(id), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning all Challenges
     *
     * @param type Which challenges should be returned. "current" for only current challenges, "past" for only past and anything else for all
     * @param request automatically filled by browser
     * @return Challenge data of all Challenges
     */
    @Operation(summary = "Get all, current or past challenges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = ChallengeDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<ChallengeDTO>> getChallenges(@Parameter(description = "Which challenges should be returned. \"current\" for only current challenges, \"past\" for only past and anything else for all") @RequestParam String type, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            List<ChallengeDTO> challenges = challengeService.getAll();

            switch(type){
                case "current":
                    List<ChallengeDTO> currentChallenges = new ArrayList<>();

                    for (ChallengeDTO challenge: challenges){
                        LocalDateTime today = LocalDateTime.now();

                        if(challenge.getEndDate().isAfter(today) && today.isAfter(challenge.getStartDate())){
                            currentChallenges.add(challenge);
                        }
                    }
                    challenges = currentChallenges;
                    break;
                case "past":
                    List<ChallengeDTO> pastChallenges = new ArrayList<>();

                    for (ChallengeDTO challenge: challenges){
                        LocalDateTime today =  LocalDateTime.now();

                        if(challenge.getEndDate().isBefore(today)){
                            pastChallenges.add(challenge);
                        }
                    }
                    challenges = pastChallenges;
                    break;
                default: //all challenges
                    break;
            }

            return new ResponseEntity<>(challenges, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Internal Problem of Swagger Ui/ Spring to upload a file and a json object
     * creates a Converter for the  Mediatype which allows octet stream,
     * so it can have a json format Object
     *
     */
    @Bean
    public MappingJackson2HttpMessageConverter octetStreamJsonConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(new MediaType("application", "octet-stream")));
        return converter;
    }

    /**
     * REST API for adding a new Challenge
     *
     * @param file Image that should be stored for the Challenge
     * @param request automatically filled by browser
     *
     * @return A 201 Code and the Challenge data if it worked 417 otherwise
     */
    @Operation(summary = "Adds the new Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Challenge successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeDTO.class))}),
            @ApiResponse(responseCode = "417", description = "Something went wrong creating the new Challenge", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<ChallengeDTO> addChallenge(@RequestPart("file") MultipartFile file, @RequestParam("sportId") long sportId[], @RequestParam("sportFactor") float sportFactor[], @RequestPart("json") @Valid ChallengeDTO challenge, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            ResponseEntity<ChallengeDTO> chDTO = null;
            if (sportId.length == sportFactor.length) {
                chDTO =   new ResponseEntity<>(challengeService.add(file,sportId,sportFactor,challenge), HttpStatus.CREATED);
                return chDTO;
            }else {
                throw new RuntimeException();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for updating a challenge
     *
     * @param ID of Challenge which should be deleted
     * @param challenge challenge data for the Challenge update
     * @param file Image which should be stored
     * @param request automatically filled by browser
     * @return A 200 Code and the Member data if it worked 404 otherwise
     */
    @Operation(summary = "Updates a challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "417", description = "Something went wrong updating the  Challenge", content = @Content),

    })
    @PutMapping(path = "/{id}/",consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<ChallengeDTO> updateChallenge(@RequestParam("imageId") long imageID, @PathVariable("id") long ID,  @RequestPart("json") @Valid ChallengeDTO challenge, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(challengeService.update(imageID, ID, challenge), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    /**
     * Rest API for deleting challenge
     *
     * @param ID Challenge with the ID should be deleted
     * @param request automatically filled by browser
     * @return A code 200 and the challenge data if it worked otherwise 404
     */
    @Operation(summary = "Delete a challenge ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping(path = "/{id}/",produces = "application/json")
    public ResponseEntity<Void> deleteChallenge(@PathVariable("id") long ID,HttpServletRequest request) throws NotFoundException {
        if (saml2Service.isLoggedIn(request)){
            try {
                challengeService.delete(ID);
                return new ResponseEntity<>(HttpStatus.OK);
            }catch (NotFoundException e){
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get all activities for a given Challenge ID
     *
     * @param challengeID The ID of the challenge
     * @param request automatically filled by browser
     * @return List of all activities if logged in
     */
    @Operation(summary = "Get all activities for given Challenge ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Activities for Challenge found.",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "No activities found.", content = @Content)
    })
    @GetMapping(path = "/{id}/activities/", produces = "application/json")
    public ResponseEntity<List<ActivityDTO>> getAllActivitiesForChallenge(@PathVariable("id") long challengeID, HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)){
            List<ActivityDTO> challengeActivities = challengeService.getActivitiesForChallenge(challengeID);

            if(!challengeActivities.isEmpty()){
                return new ResponseEntity<>(challengeActivities, HttpStatus.OK);
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
    @GetMapping(path = "/{id}/rawDistance/", produces = "application/json")
    public ResponseEntity<Float> getRawDistanceForChallenge(@PathVariable("id") long challengeID, HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)){
            try{
                List<Activity> activities = activityConverter.convertDtoListToEntityList(challengeService.getActivitiesForChallenge(challengeID));

                return new ResponseEntity<>(activityService.getRawDistanceForActivities(activities), HttpStatus.OK);
            } catch (InvalidActivitiesException | NotFoundException e){
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
    @GetMapping(path = "/{id}/distance/", produces = "application/json")
    public ResponseEntity<Float> getDistanceForChallenge(@PathVariable("id") long challengeID, HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)){
            try{
                List<Activity> activities = activityConverter.convertDtoListToEntityList(challengeService.getActivitiesForChallenge(challengeID));

                return new ResponseEntity<>(activityService.getDistanceForActivities(activities), HttpStatus.OK);
            } catch (InvalidActivitiesException | NotFoundException e){
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get all teams for a given challenge
     *
     * @param challengeID corresponding ID of Challenge
     * @param request     automatically filled by browser
     * @return 200 for success or 404 for not finding the challenge
     */
    @Operation(summary = "Get all Teams for the Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search for the teams successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TeamDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "No teams found", content = @Content)
    })
    @GetMapping(path = "/{id}/teams/", produces = "application/json")
    public ResponseEntity<List<TeamDTO>> getAllTeamsForChallenge(@PathVariable("id") long challengeID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            List<TeamDTO> teamList = challengeService.getChallengeTeams(challengeID);
            if (!teamList.isEmpty()) {
                return new ResponseEntity<>(teamList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Delete all teams in a challenge
     *
     * @param challengeID ID of the corresponding Challenge
     * @param request automatically filled by browser
     * @return 204 for success
     */
    @Operation(summary = "Delete all Teams in given Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All teams for the challenge deleted", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content)

    })
    @DeleteMapping(path = "/{id}/teams/")
    public ResponseEntity<HttpStatus> deleteTeamsFromChallenge(@PathVariable("id") long challengeID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            challengeService.deleteChallengeTeams(challengeID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Get all members in challenge
     *
     * @param challengeID ID of the corresponding Challenge
     * @param request automatically filled by browser
     * @return 200 for success else 404 for not finding Challenge
     */
    @Operation(summary = "Get all members of the Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members found.",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = MemberDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Members not found", content = @Content)
    })
    @GetMapping(path = "/{id}/members/", produces = "application/json")
    public ResponseEntity<List<MemberDTO>> getMembersForChallenge(@PathVariable("id") long challengeID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            List<MemberDTO> memberList = challengeService.getChallengeMembers(challengeID);
            if (!memberList.isEmpty()) {
                return new ResponseEntity<>(memberList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

     /* REST API for deleting all Activities
     *
     * @param request automatically filled by browser
     * @return A 200 Code if it worked
     */
    @Operation(summary = "Deletes all Challenges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Challenges successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAllChallenges(HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            challengeService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
