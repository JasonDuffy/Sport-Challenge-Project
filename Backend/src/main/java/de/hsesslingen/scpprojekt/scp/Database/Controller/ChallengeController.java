package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Service.ImageStorageService;
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
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    @GetMapping(path = "/{id}/" , produces = "application/json")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
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
     * @param type Which challenges should be returned. "current" for only current challenges, "past" for only past and anything else for all
     * @param request automatically filled by browser
     * @return Challenge data of all Challenges
     */
    @Operation(summary = "Get all, current or past challenges")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                                array = @ArraySchema(schema = @Schema(implementation = Challenge.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<Challenge>> getChallenges(@Parameter(description = "Which challenges should be returned. \"current\" for only current challenges, \"past\" for only past and anything else for all") @RequestParam String type, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            List<Challenge> challenges = challengeRepository.findAll();
            switch(type){
                case "current":
                    List<Challenge> currentChallenges = new ArrayList<>();

                    for (Challenge challenge: challenges){
                        LocalDateTime today = LocalDateTime.now();

                        if(challenge.getEndDate().isAfter(today) && today.isAfter(challenge.getStartDate())){
                            currentChallenges.add(challenge);
                        }
                    }
                    challenges = currentChallenges;
                    break;
                case "past":
                    List<Challenge> pastChallenges = new ArrayList<>();

                    for (Challenge challenge: challenges){
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
                            schema = @Schema(implementation = Challenge.class))}),
            @ApiResponse(responseCode = "417", description = "Something went wrong creating the new Challenge", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Challenge> addChallenge(@RequestParam("file") MultipartFile file, @RequestPart("json") @Valid Challenge challenge, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
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
                            schema = @Schema(implementation = Challenge.class))}),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "417", description = "Something went wrong updating the  Challenge", content = @Content),

    })
    @PutMapping(path = "/{id}/",consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Challenge> updateChallenge(@RequestParam("file") MultipartFile file, @PathVariable("id") long ID,  @RequestPart("json") @Valid Challenge challenge, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Challenge> challengeData = challengeRepository.findById(ID);
            if (challengeData.isPresent()) {
                try{
                    Image challengeImage = imageStorageService.store(file);
                    Challenge newChallenge = challengeData.get();
                    newChallenge.setName(challenge.getName());
                    newChallenge.setDescription(challenge.getDescription());
                    newChallenge.setStartDate(challenge.getStartDate());
                    newChallenge.setEndDate(challenge.getEndDate());
                    newChallenge.setImage(challengeImage);
                    newChallenge.setTargetDistance(challenge.getTargetDistance());
                    return new ResponseEntity<>(challengeRepository.save(newChallenge), HttpStatus.OK);
                }catch (Exception e){
                    return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
                }
            } else {
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
    public ResponseEntity<HttpStatus> deleteChallenge(@PathVariable("id") long ID,HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Challenge>challengeData = challengeRepository.findById(ID);
            if(challengeData.isPresent()) {
                challengeRepository.deleteById(ID);
                return new ResponseEntity<>(HttpStatus.OK);
            } else  {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
