package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Services.BonusService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportBonusService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RestController
@RequestMapping("/bonuses")
public class BonusController {
    @Autowired
    BonusService bonusService;
    @Autowired
    ChallengeSportService challengeSportService;

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
    public ResponseEntity<BonusDTO> createBonus(@RequestBody BonusDTO bonus, @RequestParam("challengesportID") long challengesportID[], HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(bonusService.add(bonus,challengesportID), HttpStatus.CREATED);
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
     * REST API for updating or adding a bonus
     *
     * @param bonus Bonus data for the Bonus update
     * @param id ID of the bonus to be updated
     * @param challengeSportID Array of ChallengeSport IDs for which the bonus should be applied
     * @param request automatically filled by browser
     * @return A 200 Code and the Bonus data if it worked 404 otherwise
     */
    @Operation(summary = "Updates or adds a new Bonus, depending on if it already exists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bonus successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BonusDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<BonusDTO> updateBonus(@PathVariable("id") long id, @RequestParam("challengeSportID") long[] challengeSportID, @RequestBody BonusDTO bonus, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(bonusService.update(id, bonus, challengeSportID), HttpStatus.OK);
            } catch (InvalidActivitiesException | NotFoundException e) {
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


    /**
     * Rest API that returns all sports for a bonus
     *
     * @param id ID of Bonus for which the Sports should be retrieved
     * @param request automatically filled by browser
     * @return List of Sports for bonus
     */
    @Operation(summary = "Get all Sports for Bonus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sports for Bonus found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/sports/" , produces = "application/json")
    public ResponseEntity<List<Sport>> getSportsForBonus(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(bonusService.getSportsForBonus(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API that returns all sports for a bonus
     *
     * @param id ID of Bonus for which the Challenge should be retrieved
     * @param request automatically filled by browser
     * @return Data of the Challenge for bonus
     */
    @Operation(summary = "Get the Challenge for Bonus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Challenge for Bonus found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sport.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/challenge/" , produces = "application/json")
    public ResponseEntity<ChallengeDTO> getChallengeForBonus(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(bonusService.getChallengeForBonus(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for returning ChallengeSport data of a given bonusID
     *
     * @param bonusID ID of the bonus that should be returned
     * @param request automatically filled by browser
     * @return ChallengeSport data corresponding for the given bonusID
     */
    @Operation(summary = "Get Challenge Sports for a given Bonus ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ChallengeSports returned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChallengeSportDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/challenge-sports/", produces = "application/json")
    public ResponseEntity<List<ChallengeSportDTO>> getChallengeSportsByBonusID(@PathVariable("id") long bonusID, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(challengeSportService.getAllChallengeSportsForBonusID(bonusID), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
