package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamMemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Rest for TeamMembers
 *
 * @author Tom Nguyen Dinh
 */

@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/teamMembers")
public class TeamMemberController {

    @Autowired
    TeamMemberService teamMemberService;
    @Autowired
    private SAML2Service saml2Service;

    /**
     * get all Teammember
     *
     * @param request automatically filled by browser
     * @return A 200 code for success
     */
    @Operation(summary = "Get all Team-Member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamMemberDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/", produces = "application/json")
    public ResponseEntity<List<TeamMemberDTO>> getALLTeamMembers(HttpServletRequest request)  {
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(teamMemberService.getAll(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
    @Operation(summary = "Get all Team-Member for a Challenge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamMemberDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/challenges/{id}/", produces = "application/json")
    public ResponseEntity<List<TeamMemberDTO>> getALLTeamMembersForChallenge(@PathVariable ("id") long ChallengeID,
                                                                             HttpServletRequest request)  {
        if (saml2Service.isLoggedIn(request)){
            return new ResponseEntity<>(teamMemberService.getAllTeamOfChallenge(ChallengeID), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest Api for getting TeamMember by ID
     *
     * @param id of Team Member
     * @param request automatically filled by browser
     * @return 200 for success else TeamMember not found 404
     */

    @Operation(summary = "Get Team-Member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Team-Member found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Team-Member not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/", produces = "application/json")
    public ResponseEntity<TeamMemberDTO> getTeamMemberByID(@PathVariable("id") long id, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(teamMemberService.get(id), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for adding a Member to a Team
     *
     * @param teamMemberDTO TeamMember data for the new TeamMember
     * @param request automatically filled by browser
     * @return 201 for successful add else 404 for not finding the team or member
     *         otherwise 500
     */

    @Operation(summary = "Adds a Member to a Team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member successfully added to Team",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamMemberDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Team and Member not found", content = @Content)
    })
    @PostMapping(path = "/",produces = "application/json")
    public ResponseEntity<TeamMemberDTO> addTeamMember( TeamMemberDTO teamMemberDTO,HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)){
            try{
                 return new ResponseEntity<>(teamMemberService.add(teamMemberDTO),HttpStatus.CREATED);
                } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for deleting a Member of a team
     *
     * @param ID of the TeamMember
     * @param request automatically filled by browser
     * @return 200 Success else 404 for not found TeamMember
     */
    @Operation(summary = "Deletes a Member of a Team ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamMember.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "TeamMember not found", content = @Content)
    })
    @DeleteMapping(path = "/{id}/",produces = "application/json")
    public ResponseEntity<HttpStatus> deleteATeamMember(@PathVariable("id") long ID,HttpServletRequest request){
        if (saml2Service.isLoggedIn(request)){
            try{
                teamMemberService.delete(ID);
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
     * Deleting all TeamMembers
     *
     * @param request automatically filled by browser
     * @return A 200 Code for Success delete
     */

    @Operation(summary = "Deletes all TeamMembers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All TeamMembers successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAllTeamMembers(HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            teamMemberService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API Update TeamMember
     *
     * @param id of TeamMember to be updated
     * @param teamMember updated Object
     * @param request  automatically filled by browser
     * @return 200 Code else 404 for TeamMember not found
     */

    @Operation(summary = "Updates a TeamMember")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TeamMember successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivityDTO.class))}),
            @ApiResponse(responseCode = "404", description = "TeamMember not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<TeamMemberDTO> updateTeamMember(@PathVariable("id") long id, @RequestBody TeamMemberDTO teamMember, HttpServletRequest request) {
        if (saml2Service.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(teamMemberService.update(id, teamMember), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
