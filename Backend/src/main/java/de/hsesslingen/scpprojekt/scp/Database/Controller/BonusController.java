package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
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
 * REST controller for Bonus.
 *
 * @author Jason Patrick Duffy
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/bonuses")
public class BonusController {

    @Autowired
    BonusRepository bonusRepository;

    @Autowired
    ChallengeSportRepository challengeSportRepository;

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
                            schema = @Schema(implementation = Bonus.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<Bonus>> getBonuses(HttpServletRequest request){
        if (SAML2Functions.isLoggedIn(request)){
            List<Bonus> bonuses = bonusRepository.findAll();
            return new ResponseEntity<>(bonuses, HttpStatus.OK);
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
                            schema = @Schema(implementation = Bonus.class))}),
            @ApiResponse(responseCode = "404", description = "Bonus not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/", produces = "application/json")
    public ResponseEntity<Bonus> getBonusByID(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Bonus> bonusData = bonusRepository.findById(id);
            if (bonusData.isPresent()) {
                return new ResponseEntity<>(bonusData.get(), HttpStatus.OK);
            } else {
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
                            schema = @Schema(implementation = Bonus.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "ChallengeSport not found", content = @Content)
    })
    @PostMapping(path = "/", produces = "application/json")
    public ResponseEntity<Bonus> createBonus(@RequestParam Long challengeSportID, @RequestBody Bonus bonus, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<ChallengeSport> challSport = challengeSportRepository.findById(challengeSportID);

            if (challSport.isPresent()){
                Bonus newBonus = bonusRepository.save(new Bonus(challSport.get(), bonus.getStartDate(), bonus.getEndDate(), bonus.getFactor(), bonus.getName(), bonus.getDescription()));
                return new ResponseEntity<>(newBonus, HttpStatus.CREATED);
            }
            else{
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
                            schema = @Schema(implementation = Bonus.class))}),
            @ApiResponse(responseCode = "404", description = "Bonus not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<Bonus> updateBonus(@PathVariable("id") long id, @RequestBody Bonus bonus, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Bonus> bonusData = bonusRepository.findById(id);

            if (bonusData.isPresent()) {
                Bonus newBonus = bonusData.get();
                newBonus.setFactor(bonus.getFactor());
                newBonus.setStartDate(bonus.getStartDate());
                newBonus.setEndDate(bonus.getEndDate());
                newBonus.setDescription(bonus.getDescription());
                newBonus.setName(bonus.getName());
                newBonus.setChallengeSport(bonus.getChallengeSport());
                return new ResponseEntity<>(bonusRepository.save(newBonus), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Bonus> bonusData = bonusRepository.findById(id);
            if (bonusData.isPresent()){
                bonusRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
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
        if (SAML2Functions.isLoggedIn(request)){
            bonusRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
