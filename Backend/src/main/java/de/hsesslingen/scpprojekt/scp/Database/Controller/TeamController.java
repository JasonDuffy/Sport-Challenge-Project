package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.DTO.TeamDTO;
import de.hsesslingen.scpprojekt.scp.Database.Service.TeamService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * Rest controller for Teams
 *
 * @author Tom Nguyen Dinh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    TeamService teamService;

    /**
     * Rest API for creating a Team for a challenge
     *
     * @param file  Image that should be stored for the Team
     * @param team  team with the corresponding Data
     * @param request automatically filled by browser
     * @return 201 for success else 404 for mot found or 417 something went wrong
     */
    @Operation(summary = "Adds a team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Team successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))}),
            @ApiResponse(responseCode = "417", description = "Something went wrong creating the Team", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content)
    })
    @PostMapping(path = "/", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<TeamDTO> addTeam(@RequestParam("file")MultipartFile file,
                                        @RequestPart("json") @Valid TeamDTO team,
                                        HttpServletRequest request){
        if (SAML2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(teamService.add(file,team), HttpStatus.CREATED);
            }catch (NotFoundException e){
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        }else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for updating a Team
     *
     * @param file  Image that should be stored for the Team
     * @param TeamID ID of the Team which should be updated
     * @param team new Data of the team
     * @param request automatically filled by browser
     * @return 200 for successful update else 404 Team not found or 417 for something went wrong
     */
    @Operation(summary = "Updates a team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "417", description = "Something went wrong updating the Team", content = @Content)
    })
    @PutMapping(path = "/{id}/",consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<TeamDTO> updateTeam(@RequestParam("file")MultipartFile file,
                                           @PathVariable("id") long TeamID,
                                           @RequestPart("json") @Valid TeamDTO team,
                                           HttpServletRequest request){
        if (SAML2Service.isLoggedIn(request)){
           try{
               return new ResponseEntity<>(teamService.update(file,TeamID,team), HttpStatus.OK);
            }catch (NotFoundException e){
               System.out.println((e.getMessage()));
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for deleting a team
     *
     * @param ID of the Team which should be deleted
     * @param request automatically filled by browser
     * @return 200 success , 404 Team is not found
     */
    @Operation(summary = "Delete a team ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Team not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping(path = "/{id}/",produces = "application/json")
    public ResponseEntity<HttpStatus> deleteTeam(@PathVariable("id")long ID,HttpServletRequest request){
        if (SAML2Service.isLoggedIn(request)){
            try{
                teamService.delete(ID);
                return new ResponseEntity<>(HttpStatus.OK);
            }catch (NotFoundException e){
                System.out.println((e.getMessage()));
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    /**
     * REST APi for get all Teams
     * @param request automatically filled by browser
     * @return 200 for success
     */

    @Operation(summary = "Get all Teams ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TeamDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<TeamDTO>> getAllTeams(HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            return new ResponseEntity<>(teamService.getAll(),HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest APi for searching a Team by ID
     *
     * @param TeamID ID of the corresponding Team
     * @param request automatically filled by browser
     * @return 200 for successful Search or 404 for not finding Challenge
     */
    @Operation(summary = "Get Team by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TeamDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Team not found in the challenge", content = @Content),
    })
    @GetMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<TeamDTO> getTeamByID(@PathVariable("id") long TeamID, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(teamService.get(TeamID),HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest Api for deleting all teams
     *
     * @param request automatically filled by browser
     * @return 200 for success else 500
     */

    @Operation(summary = "Deletes all Teams")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All teams successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong deleting all teams", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<HttpStatus> deleteAllMembers(HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            try {
                teamService.deleteAll();
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}