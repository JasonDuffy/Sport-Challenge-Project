package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.Services.BonusService;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
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

/**
 * REST controller for Bonus.
 *
 * @author Jason Patrick Duffy
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/bonuses")
public class BonusController {
    @Autowired
    BonusService bonusService;

    @Autowired
    SAML2Service saml2Service;

    /**
     * REST API for returning all bonuses
     *
     * @param request automatically filled by browser
     * @return List of all bonuses if logged in
     */
    @Operation(summary = "Get all bonuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonusDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<BonusDTO>> getBonuses(HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(bonusService.getAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning Bonus data of a given ID
     *
     * @param id ID of the Bonus that should be returned
     * @param request automatically filled by browser
     * @return Bonus data corresponding for the given ID, 404 otherwise
     */
    @Operation(summary = "Get bonus by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bonus found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonusDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Bonus not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/", produces = "application/json")
    public ResponseEntity<BonusDTO> getBonusByID(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(bonusService.get(id), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for adding a new Bonus
     *
     * @param bonus Bonus data for the new bonus
     * @param request automatically filled by browser
     * @return A 201 Code and the Bonus data if it worked, 500 otherwise
     */
    @Operation(summary = "Add new Bonus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bonus successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonusDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "ChallengeSport not found", content = @Content)
    })
    @PostMapping(path = "/", produces = "application/json")
    public ResponseEntity<BonusDTO> createBonus(@RequestBody BonusDTO bonus, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(bonusService.add(bonus), HttpStatus.CREATED);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for updating a bonus
     *
     * @param bonus Bonus data for the Bonus update
     * @param request automatically filled by browser
     * @return A 200 Code and the Bonus data if it worked 404 otherwise
     */
    @Operation(summary = "Updates a Bonus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bonus successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonusDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Bonus not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<BonusDTO> updateBonus(@PathVariable("id") long id, @RequestBody BonusDTO bonus, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(bonusService.update(id, bonus), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (InvalidActivitiesException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for deleting a Bonus
     *
     * @param id ID of the Bonus that should be deleted
     * @param request automatically filled by browser
     * @return A 200 Code
     * otherwise if Bonus not found 404
     */
    @Operation(summary = "Deletes a Bonus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bonus successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bonus not found", content = @Content)

    })
    @DeleteMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<Void> deleteBonus(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                bonusService.delete(id);
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
     * REST API for deleting all Bonuses
     *
     * @param request automatically filled by browser
     * @return A 200 Code it worked
     */
    @Operation(summary = "Deletes all Bonuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Bonuses successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAllBonuses(HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            bonusService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
