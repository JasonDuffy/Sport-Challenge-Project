package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
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
 * REST controller for ChallengeSport.
 *
 * @author Robin Hackh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/challenge-sports")
public class ChallengeSportController {
    @Autowired
    private ChallengeSportRepository challengeSportRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private SportRepository sportRepository;

    /**
     * REST API for returning ChallengeSport data of a given ID
     *
     * @param id ID of the ChallengeSport that should be returned
     * @param request automatically filled by browser
     * @return ChallengeSport data corresponding to the given ID 404 otherwise
     */
    @Operation(summary = "Get challenge-sport by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ChallengeSport found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeSport.class))}),
            @ApiResponse(responseCode = "404", description = "ChallengeSport not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/" , produces = "application/json")
    public ResponseEntity<ChallengeSport> getChallengeSportById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<ChallengeSport> challengeSportData = challengeSportRepository.findById(id);
            if (challengeSportData.isPresent()) {
                return new ResponseEntity<>(challengeSportData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning all ChallengeSport data
     *
     * @param request automatically filled by browser
     * @return ChallengeSport data of all DB entries
     */
    @Operation(summary = "Get all challenge-sports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeSport.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/" , produces = "application/json")
    public ResponseEntity<List<ChallengeSport>> getAllChallengeSports(HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            List<ChallengeSport> challengeSports = challengeSportRepository.findAll();
            return new ResponseEntity<>(challengeSports, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for adding a new ChallengeSport
     *
     * @param factor Factor for the ChallengeSport
     * @param sportId Foreign key of the sport
     * @param challengeId Foreign key of the challenge
     * @param request automatically filled by browser
     * @return A 201 Code and the sport data if it worked 417 otherwise
     */
    @Operation(summary = "Adds the new challengeSport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "ChallengeSport successfully added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "500", description = "Something went wrong creating the new ChallengeSport", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/",produces = "application/json")
    public ResponseEntity<ChallengeSport>addChallengeSport(@RequestParam float factor, @RequestParam long sportId, @RequestParam long challengeId, HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            try {
                Optional<Sport> foreignSport = sportRepository.findById(sportId);
                Optional<Challenge> foreignChallenge = challengeRepository.findById(challengeId);

                if(foreignSport.isPresent() && foreignChallenge.isPresent()){
                    ChallengeSport newChallengeSport = challengeSportRepository.save(new ChallengeSport(factor, foreignChallenge.get(), foreignSport.get()));
                    return new ResponseEntity<>(newChallengeSport, HttpStatus.CREATED);
                }else{
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    /**
     * Rest API for Deleting of ChallengeSport
     *
     * @param id get ChallengeSportId
     * @param request automatically filled by browser
     * @return 204 for Deleted Sport if not 404
     */
    @Operation(summary = "Deleted the ChallengeSport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted ChallengeSport"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "ChallengeSport not found", content = @Content)
    })
    @DeleteMapping(path = "/{id}/")
    public ResponseEntity<Void> deleteChallengeSport(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<ChallengeSport> challengeSportData = challengeSportRepository.findById(id);
            if (challengeSportData.isPresent()){
                challengeSportRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for updating a ChallengeSport
     *
     * @param id of the ChallengeSport which should be updated
     * @param factor Factor for the ChallengeSport update
     * @param sportId Foreign key of the sport update
     * @param challengeId Foreign key of the challenge update
     * @param request automatically filled by browser
     * @return A 200 Code and the ChallengeSport data if it worked, 404 otherwise
     */
    @Operation(summary = "Updates a ChallengeSport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ChallengeSport successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "404", description = "ChallengeSport not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<ChallengeSport> updateChallenge(@PathVariable("id") long id, @RequestParam float factor, @RequestParam long sportId, @RequestParam long challengeId, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<ChallengeSport> challengeSportData = challengeSportRepository.findById(id);
            Optional<Sport> foreignSportData = sportRepository.findById(sportId);
            Optional<Challenge> foreignChallengeData = challengeRepository.findById(challengeId);

            if (challengeSportData.isPresent() && foreignChallengeData.isPresent() && foreignSportData.isPresent()) {
                ChallengeSport newChallengeSport = challengeSportData.get();
                newChallengeSport.setFactor(factor);
                newChallengeSport.setChallenge(foreignChallengeData.get());
                newChallengeSport.setSport(foreignSportData.get());

                return new ResponseEntity<>(challengeSportRepository.save(newChallengeSport), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
