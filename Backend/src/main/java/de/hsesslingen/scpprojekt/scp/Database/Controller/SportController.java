package de.hsesslingen.scpprojekt.scp.Database.Controller;
import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.SportService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
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
 * @author Tom Nguyen Dinh, Jason Patrick Duffy
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/sports")
public class SportController {

    @Autowired
    private SportService sportService;

    /**
     * Rest API return Sport of a given id
     *
     * @param sport  sport data for the created one
     * @param request automatically filled by browser
     * @return A 201 Code for success else 403 fo not logged in
     */
    @Operation(summary = "Adds the new sport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sport successfully added",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/",produces = "application/json")
    public ResponseEntity<Sport>addSport(@RequestBody Sport sport, HttpServletRequest request){
        if (SAML2Service.isLoggedIn(request)){
            return new ResponseEntity<>(sportService.add(sport),HttpStatus.CREATED);
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
    @Operation(summary = "Get sport by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sport found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "404", description = "Sport not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/" , produces = "application/json")
    public ResponseEntity<Sport> getSportById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
           try{
               return new ResponseEntity<>(sportService.get(id), HttpStatus.OK);
           }catch (NotFoundException e){
               System.out.println(e.getMessage());
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
     * @return Sport data of all sports
     */
    @Operation(summary = "Get all sports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Sport.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<Sport>> getAllSports(HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            return new ResponseEntity<>(sportService.getAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     *Rest Api for Deleting of sport
     *
     * @param id get SportID
     * @param request automatically filled by browser
     * @return 200 for Deleted Sport if not 404
     */
    @Operation(summary = "Deleted the sport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted sport"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Sport not found", content = @Content)
    })
    @DeleteMapping(path = "/{id}/")
    public ResponseEntity<HttpStatus> deleteSport(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            try{
                sportService.delete(id);
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
     * Rest Api  for Deleting ALl sports
     *
     * @param request automatically filled by browser
     * @return 200 for success
     */

    @Operation(summary = "Deleted all sports")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted aLL sport"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
    })
    @DeleteMapping(path = "/")
    public ResponseEntity<HttpStatus> deleteAllSport(HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            sportService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     *Rest API for updating a sport
     *
     * @param ID of the sport which should be updated
     * @param sport Sport data for Sport update
     * @param request automatically filled by browser
     * @return A 200 Code and the Sport data if it worked,  404 otherwise
     */
    @Operation(summary = "Updates a sport")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "sport successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "404", description = "Sport not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<Sport> updateChallenge(@PathVariable("id") Long ID, @RequestBody Sport sport, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(sportService.update(ID,sport), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
