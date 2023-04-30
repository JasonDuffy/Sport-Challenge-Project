package de.hsesslingen.scpprojekt.scp.Database.Controller;
import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.List;
import java.util.Optional;

/**
 * Rest Controller for Sport
 *
 * @author Tom Nguyen Dinh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/Sport")
public class SportController {

    @Autowired
    private SportRepository sportRepository;

    /**
     * Rest API return Sport of a given id
     *
     * @param sport  sport data for the created one
     * @param request automatically filled by browser
     * @return A 201 Code and the Challenge data if it worked 417 otherwise
     */
    @Operation(summary = "Adds the new Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sport successfully added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "417", description = "Something went wrong creating the new sport", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/addSport",produces = "application/json")
    public ResponseEntity<Sport>addSport(@RequestBody Sport sport, HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            try {
                Sport newsport = sportRepository.save(
                        new Sport( sport.getName(),sport.getFactor()));
                return new ResponseEntity<>(newsport, HttpStatus.CREATED);
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    /**
     * Rest API return Sport of a given id
     *
     * @param id  id of Sport
     * @param request automatically filled by browser
     * @return Sport data corresponding to the given ID 404 otherwise
     */
    @Operation(summary = "Get challenge by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sport found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "404", description = "Sport not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}" , produces = "application/json")
    public ResponseEntity<Sport> getChallengeById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Sport> sportData = sportRepository.findById(id);
            if (sportData.isPresent()) {
                return new ResponseEntity<>(sportData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for Returning all Sport
     *
     *  @param request automatically filled by browser
     * @return Challenge data of all Challenges
     */
    @Operation(summary = "Get all sports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All sports that were found",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Sport.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/getAllSports", produces = "application/json")
    public ResponseEntity<List<Sport>> getAllSports(HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            List<Sport> sports = sportRepository.findAll();
            return new ResponseEntity<>(sports, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     *Rest Api for Deleting of sport
     *
     * @param id get SportID
     * @param request automatically filled by browser
     * @return Signal for Deletion
     */
    @Operation(summary = "Deleted the sport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted sport",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Challenge.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<HttpStatus> deleteSport(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try {
                sportRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
    }


}
