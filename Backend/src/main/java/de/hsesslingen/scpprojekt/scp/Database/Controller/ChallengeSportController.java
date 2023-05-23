package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
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
import java.util.Optional;

/**
 * REST controller for ChallengeSport.
 *
 * @author Robin Hackh, Tom Nguyen Dinh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/challenge-sports")
public class ChallengeSportController {
    @Autowired
    private ChallengeSportService challengeSportService;

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
                            schema = @Schema(implementation = ChallengeSportDTO.class))}),
            @ApiResponse(responseCode = "404", description = "ChallengeSport not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/" , produces = "application/json")
    public ResponseEntity<ChallengeSportDTO> getChallengeSportById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(challengeSportService.getDTO(id), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
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
                            schema = @Schema(implementation = ChallengeSportDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/" , produces = "application/json")
    public ResponseEntity<List<ChallengeSportDTO>> getAllChallengeSports(HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            return new ResponseEntity<>(challengeSportService.getAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for adding a new ChallengeSport
     *
     * @param challengeSportDTO challengeSportDTO  data for the new challengeSport
     * @param request automatically filled by browser
     * @return A 201 Code and the sport data if it worked 417 otherwise
     */
    @Operation(summary = "Adds the new challengeSport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "ChallengeSport successfully added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeSportDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Something went wrong creating the new ChallengeSport", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/",produces = "application/json")
    public ResponseEntity<ChallengeSportDTO>addChallengeSport(@RequestBody ChallengeSportDTO challengeSportDTO, HttpServletRequest request){
        if (SAML2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(challengeSportService.add(challengeSportDTO), HttpStatus.CREATED);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
        if (SAML2Service.isLoggedIn(request)){
            try{
                challengeSportService.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Deletes all ChallengeSport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All ChallengeSport successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAllActivities(HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            challengeSportService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    /**
     * Rest API for updating a ChallengeSport
     *
     * @param Cs data of ChallengeSport for the update
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
    public ResponseEntity<ChallengeSportDTO> updateChallenge(@PathVariable("id") long id,@RequestBody ChallengeSportDTO Cs, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(challengeSportService.update(id, Cs), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning all Challenge-Sports for the given Challenge ID
     *
     * @param ChallengeID ID of the Challenge where all challenge-sports should be returned
     * @param request automatically filled by browser
     * @return A 200 Code if it worked
     */
    @Operation(summary = "Get all Challenge-Sport for a Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ChallengeSportDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/challenges/{id}/", produces = "application/json")
    public ResponseEntity<List<ChallengeSportDTO>> getAllChallengeSportsForChallenge(@PathVariable("id") long ChallengeID, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            return new ResponseEntity<>(challengeSportService.getAllChallengeSportsOfChallenge(ChallengeID), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
