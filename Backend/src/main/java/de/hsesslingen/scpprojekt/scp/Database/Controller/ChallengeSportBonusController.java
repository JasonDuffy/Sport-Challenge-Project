package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportBonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportBonusService;
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
 * Rest Controller for ChallengeSportBonus
 *
 * @author Robin Hackh
 */
@RestController
@RequestMapping("/challenge-sport-bonuses")
public class ChallengeSportBonusController {

    @Autowired
    ChallengeSportBonusService challengeSportBonusService;
    @Autowired
    SAML2Service saml2Service;

    /**
     * REST API for returning all ChallengeSportBonuses
     *
     * @param request automatically filled by browser
     * @return List of all ChallengeSportBonuses if logged in
     */
    @Operation(summary = "Get all ChallengeSportBonuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeSportBonusDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<ChallengeSportBonusDTO>> getAllChallengeSportBonus(HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(challengeSportBonusService.getAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning ChallengeSportBonus data of a given ID
     *
     * @param id ID of the ChallengeSportBonus that should be returned
     * @param request automatically filled by browser
     * @return ChallengeSportBonus data corresponding for the given ID, 404 otherwise
     */
    @Operation(summary = "Get ChallengeSportBonus by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ChallengeSportBonus found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeSportBonusDTO.class))}),
            @ApiResponse(responseCode = "404", description = "ChallengeSportBonus not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/", produces = "application/json")
    public ResponseEntity<ChallengeSportBonusDTO> getChallengeSportBonusByID(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(challengeSportBonusService.get(id), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for adding a new ChallengeSportBonus
     *
     * @param challengeSportBonusDTO ChallengeSportBonus data for the new ChallengeSportBonus
     * @param request automatically filled by browser
     * @return A 201 Code and the ChallengeSportBonus data if it worked, 500 otherwise
     */
    @Operation(summary = "Add new ChallengeSportBonus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "ChallengeSportBonus successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeSportBonusDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "ChallengeSportBonus not found", content = @Content)
    })
    @PostMapping(path = "/", produces = "application/json")
    public ResponseEntity<ChallengeSportBonusDTO> addChallengeSportBonus(@RequestBody ChallengeSportBonusDTO challengeSportBonusDTO, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(challengeSportBonusService.add(challengeSportBonusDTO), HttpStatus.CREATED);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for updating a ChallengeSportBonus
     *
     * @param challengeSportBonusDTO ChallengeSportBonus data for the ChallengeSportBonus update
     * @param request automatically filled by browser
     * @return A 200 Code and the ChallengeSportBonus data if it worked 404 otherwise
     */
    @Operation(summary = "Updates a ChallengeSportBonus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ChallengeSportBonus successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeSportBonusDTO.class))}),
            @ApiResponse(responseCode = "404", description = "ChallengeSportBonus not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<ChallengeSportBonusDTO> updateChallengeSportBonus(@PathVariable("id") long id, @RequestBody ChallengeSportBonusDTO challengeSportBonusDTO, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(challengeSportBonusService.update(id, challengeSportBonusDTO), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for deleting a ChallengeSportBonus
     *
     * @param id ID of the ChallengeSportBonus that should be deleted
     * @param request automatically filled by browser
     * @return A 200 Code otherwise if ChallengeSportBonus not found 404
     */
    @Operation(summary = "Deletes a ChallengeSportBonus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ChallengeSportBonus successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "ChallengeSportBonus not found", content = @Content)
    })
    @DeleteMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<Void> deleteChallengeSportBonus(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                challengeSportBonusService.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
