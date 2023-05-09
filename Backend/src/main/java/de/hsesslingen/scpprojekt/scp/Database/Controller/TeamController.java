package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import de.hsesslingen.scpprojekt.scp.Database.Service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Rest controller for Teams
 *
 * @author Tom Nguyen Dinh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/teams")
public class TeamController {
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private  ImageStorageService imageStorageService;
    @Autowired
    private ChallengeRepository challengeRepository;

    /**
     * Rest API for creating a Team for a challenge
     *
     * @param file  Image that should be stored for the Team
     * @param challengeID ID of the challenge where the team is in
     * @param team  team with the corresponding Data
     * @param request automatically filled by browser
     * @return 201 for success else 404 for mot found or 417 something went wrong
     */
    @Operation(summary = "Adds a team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Team successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Team.class))}),
            @ApiResponse(responseCode = "417", description = "Something went wrong creating the Team", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content)
    })
    @PostMapping(path = "/", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Team> addTeam(@RequestParam("file")MultipartFile file,
                                        @RequestParam long challengeID,
                                        @RequestPart("json") @Valid Team team,
                                        HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            try{
                Optional<Challenge> challenge1 = challengeRepository.findById(challengeID);
                if(challenge1.isPresent()) {
                    Image teamImage = imageStorageService.store(file);
                    Team newteam = teamRepository.save(
                            new Team(team.getName(), teamImage, challenge1.get()));
                    return new ResponseEntity<>(newteam, HttpStatus.CREATED);
                }else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        }else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for updating a Team
     *
     * @param file  Image that should be stored for the Team
     * @param TeamID ID of the Team which should be updated
     * @param team new Data of the team
     * @param request automatically filled by browser
     * @return 200 for successful update else 404 Team not found or 417 for something went wrong
     */
    @Operation(summary = "Updates a team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Team.class))}),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "417", description = "Something went wrong updating the Team", content = @Content)
    })
    @PutMapping(path = "/{id}/",consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Team> updateTeam(@RequestParam("file")MultipartFile file,
                                           @RequestParam long TeamID,
                                           @RequestParam long ChallengeID,
                                           @RequestPart("json") @Valid Team team,
                                           HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Team> teamData = teamRepository.findById(TeamID);
            Optional<Challenge> challengeData = challengeRepository.findById(ChallengeID);
            if (teamData.isPresent()&& challengeData.isPresent()){
                try {
                    Image teamImage = imageStorageService.store(file);
                    Team newTeam = teamData.get();
                    newTeam.setName(team.getName());
                    newTeam.setImage(teamImage);
                    newTeam.setChallenge(challengeData.get());
                    return new ResponseEntity<>(teamRepository.save(newTeam),HttpStatus.OK);
                } catch (Exception e) {
                    return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
                }
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for deleting a team
     *
     * @param ID of the Team which should be deleted
     * @param request automatically filled by browser
     * @return 200 success , 404 Team is not found
     */
    @Operation(summary = "Delete a team ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping(path = "/{id}/",produces = "application/json")
    public ResponseEntity<HttpStatus> deleteTeam(@PathVariable("id") long ID,HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Team>teamData = teamRepository.findById(ID);
            if(teamData.isPresent()) {
                teamRepository.deleteById(ID);
                return new ResponseEntity<>(HttpStatus.OK);
            } else  {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    /**
     * REST APi for get all Teams
     * @param request automatically filled by browser
     * @return 200 for success
     */

    @Operation(summary = "Get all Teams ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Challenge.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<Team>> getAllTeams(HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
                List<Team> teams = teamRepository.findAll();
                return new ResponseEntity<>(teams, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest APi for searching a Team by ID
     *
     * @param TeamID ID of the corresponding Team
     * @param request automatically filled by browser
     * @return 200 for successful Search or 404 for not finding Challenge
     */
    @Operation(summary = "Get Team by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Challenge.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Team not found in the challenge", content = @Content),
    })
    @GetMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<Team> getTeamByID(@PathVariable("id") long TeamID, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Team> team = teamRepository.findById(TeamID);
                if(team.isPresent()){
                    return new ResponseEntity<>(team.get(),HttpStatus.OK);
                }else {
                    return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Deletes all Teams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All teams successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong deleting all teams", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<HttpStatus> deleteAllMembers(HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try {
                teamRepository.deleteAll();
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}