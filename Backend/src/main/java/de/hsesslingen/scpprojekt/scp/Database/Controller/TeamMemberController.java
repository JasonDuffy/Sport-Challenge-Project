package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Authentication.Services.SAML2Service;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Team;
import de.hsesslingen.scpprojekt.scp.Database.Entities.TeamMember;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamMemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
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
    TeamRepository teamRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamMemberRepository teamMemberRepository;

    /**
     *Rest API for adding a Member to a Team
     *
     * @param TeamID ID of the Team
     * @param MemberID ID of the Member who should be added to the Team
     * @param request automatically filled by browser
     * @return 201 fpr successful add else 404 for not finding the team or member
     *         otherwise 500
     */
    @Operation(summary = "Adds a Member to a Team")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member successfully added to Team",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamMember.class))}),
            @ApiResponse(responseCode = "500", description = "Something went wrong adding the Team to Member", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Team and Member not found", content = @Content)
    })
    @PostMapping(path = "/",produces = "application/json")
    public ResponseEntity<TeamMember> addTeamMember(@RequestParam long TeamID, @RequestParam long MemberID, HttpServletRequest request){
        if (SAML2Service.isLoggedIn(request)){
            try{
                Optional<Member> memberOptional = memberRepository.findById(MemberID);
                Optional<Team> teamOptional = teamRepository.findById(TeamID);
                if(memberOptional.isPresent() && teamOptional.isPresent()){
                    TeamMember newteamMember = teamMemberRepository.save(new TeamMember(teamOptional.get(),memberOptional.get()));
                    return new ResponseEntity<>(newteamMember,HttpStatus.CREATED);
                }else{
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }catch (Exception e){
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Rest API for getting all Member of a team
     *
     * @param TeamID ID of the team
     * @param request automatically filled by browser
     * @return 200 for success
     */
    @Operation(summary = "Get all Team members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamMember.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path = "/{id}/" , produces = "application/json")
    public ResponseEntity<List<TeamMember>> getAllTeamMembers(@PathVariable("id")long TeamID, HttpServletRequest request) {
        if (SAML2Service.isLoggedIn(request)){
            Optional<Team> teamOptional = teamRepository.findById(TeamID);
            if(teamOptional.isPresent()){
                List<TeamMember> teamMembers = teamMemberRepository.findAll();
                List<TeamMember> teamMemberData = new ArrayList<>();
                for(TeamMember teamMember : teamMembers){
                    if(teamMember.getTeam().getId()== TeamID) {
                        teamMemberData.add(teamMember);
                    }
                }
                return new ResponseEntity<>(teamMemberData,HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
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
    @Operation(summary = "Deletes a TeamMember")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TeamMember.class))}),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "TeamMember not found", content = @Content)
    })
    @DeleteMapping(path = "/{id}/",produces = "application/json")
    public ResponseEntity<HttpStatus> deleteATeamMember(@PathVariable("id") long ID,HttpServletRequest request){
        if (SAML2Service.isLoggedIn(request)){
            Optional<TeamMember>teamData = teamMemberRepository.findById(ID);
            if(teamData.isPresent()) {
                teamMemberRepository.deleteById(ID);
                return new ResponseEntity<>(HttpStatus.OK);
            } else  {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
