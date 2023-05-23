package de.hsesslingen.scpprojekt.scp.API.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
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
import java.util.Optional;

/**
 * REST controller for calls that depend on multiple DBs
 *
 * @author Tom Nguyen Dinh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/API")
public class APIController {
    @Autowired
    SAML2Service saml2Service;
    @Autowired
    ChallengeRepository challengeRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TeamMemberRepository teamMemberRepository;

    /**
     * Rest API for Get all Teams for a challenge
     *
     * @param ChallengeID corresponding ID of Challenge
     * @param request     automatically filled by browser
     * @return 200 for success or 404 for not finding the challenge
     */
    @Operation(summary = "Get all Teams for the Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search for the teams successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Challenge.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge Not Found", content = @Content)
    })
    @GetMapping(path = "/TeamsForChallenges/", produces = "application/json")
    public ResponseEntity<List<Team>> getAllTeamsForChallenge(@RequestParam long ChallengeID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            Optional<Challenge> challenge = challengeRepository.findById(ChallengeID);
            if (challenge.isPresent()) {
                List<Team> teams = teamRepository.findAll();
                List<Team> teamChallenge = new ArrayList<>();
                for (Team team : teams) {
                    if (team.getChallenge().getId() == ChallengeID) {
                        teamChallenge.add(team);
                    }
                }
                return new ResponseEntity<>(teamChallenge, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     *
     * @param ChallengeID ID of the corresponding Challenge
     * @param request automatically filled by browser
     * @return 200 for success else 404 for not  finding Challenge
     */
    @Operation(summary = "Delete all Teams fo this Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All teams for the challenge deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Team.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content)

    })
    @DeleteMapping(path = "/TeamsChallenge/", produces = "application/json")
    public ResponseEntity<HttpStatus> deleteTeamsFromChallenge(@RequestParam long ChallengeID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            Optional<Challenge> challenge = challengeRepository.findById(ChallengeID);
            if (challenge.isPresent()) {
                List<Team> team = teamRepository.findAll();
                for (Team team1 : team) {
                    if (team1.getChallenge().getId() == ChallengeID) {
                        teamRepository.deleteById(team1.getId());
                    }
                }
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     *
     * @param ChallengeID ID of the corresponding Challenge
     * @param request automatically filled by browser
     * @return 200 for success else 404 for not finding Challenge
     */
    @Operation(summary = "Get all Team Member for the Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search for the teams successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Challenge.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge Not Found", content = @Content)
    })
    @GetMapping(path = "/MemberChallenges/{id}/", produces = "application/json")
    public ResponseEntity<List<TeamMember>> getTeamMembersForChallenge(@PathVariable("id") long ChallengeID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)) {
            Optional<Challenge> challenge = challengeRepository.findById(ChallengeID);
            if (challenge.isPresent()) {
                List<TeamMember> teams = teamMemberRepository.findAll();
                List<TeamMember> MemberTeam = new ArrayList<>();
                for (TeamMember teamMember : teams) {
                    if (teamMember.getTeam().getChallenge().getId() == ChallengeID) {
                        MemberTeam.add(teamMember);
                    }
                }
                return new ResponseEntity<>(MemberTeam, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
