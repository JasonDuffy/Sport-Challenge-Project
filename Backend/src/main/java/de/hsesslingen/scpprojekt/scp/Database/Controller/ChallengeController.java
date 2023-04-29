package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Controller.SAML2Controller;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for Challenge.
 *
 * @author Robin Hackh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/Challenge")
public class ChallengeController {

    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private ChallengeRepository challengeRepository;

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
                            schema = @Schema(implementation = Challenge.class))}),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            Optional<Challenge> challengeData = challengeRepository.findById(id);
            if (challengeData.isPresent()) {
                return new ResponseEntity<>(challengeData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning all Challenges
     *
     * @param request automatically filled by browser
     * @return Challenge data of all Challenges
     */
    @Operation(summary = "Get all challenges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All challenges that were found",
                    content = {@Content(mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = Challenge.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping("/getAllChallenges")
    public ResponseEntity<List<Challenge>> getAllChallenges(HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            List<Challenge> challenges = challengeRepository.findAll();
            return new ResponseEntity<>(challenges, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning all current Challenges
     *
     * @param request automatically filled by browser
     * @return Challenge data of all current Challenges
     */
    @Operation(summary = "Get current challenges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current challenges",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Challenge.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping("/getCurrentChallenges")
    public ResponseEntity<List<Challenge>> getCurrentChallenges(HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            List<Challenge> challenges = challengeRepository.findAll();
            List<Challenge> currentChallenges = new ArrayList<>();

            for (Challenge challenge: challenges){
                Date today = new Date();

                if(challenge.getEndDate().before(today)){
                    currentChallenges.add(challenge);
                }
            }
            return new ResponseEntity<>(currentChallenges, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning all past Challenges
     *
     * @param request automatically filled by browser
     * @return Challenge data of all past Challenges
     */
    @Operation(summary = "Get past challenges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Past challenges",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Challenge.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping("/getPastChallenges")
    public ResponseEntity<List<Challenge>> getPastChallenges(HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            List<Challenge> challenges = challengeRepository.findAll();
            List<Challenge> pastChallenges = new ArrayList<>();

            for (Challenge challenge: challenges){
                Date today = new Date();

                if(challenge.getEndDate().after(today)){
                    pastChallenges.add(challenge);
                }
            }
            return new ResponseEntity<>(pastChallenges, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for adding a new Challenge
     *
     * @param file Image that should be stored for the Challenge
     * @param request automatically filled by browser
     * @param challenge Challenge data for the new Challenge
     * @return A 201 Code and the Challenge data if it worked 417 otherwise
     */
    @Operation(summary = "Adds the new Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Challenge successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Challenge.class))}),
            @ApiResponse(responseCode = "417", description = "Something went wrong creating the new Challenge", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/addChallenge", consumes = "multipart/form-data")
    public ResponseEntity<Challenge> addChallenge(@RequestParam("file") MultipartFile file, @RequestBody Challenge challenge, HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            try {
                Image challengeImage = imageStorageService.store(file);

                Challenge newChallenge = challengeRepository.save(
                        new Challenge(challenge.getName(), challenge.getDescription(), challenge.getStartDate(), challenge.getEndDate(), challengeImage, challenge.getTargetDistance()));
                return new ResponseEntity<>(newChallenge, HttpStatus.CREATED);
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
