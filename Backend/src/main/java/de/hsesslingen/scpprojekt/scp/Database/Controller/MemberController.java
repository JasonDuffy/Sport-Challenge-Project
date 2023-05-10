package de.hsesslingen.scpprojekt.scp.Database.Controller;

import java.util.Optional;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import de.hsesslingen.scpprojekt.scp.Database.Service.MemberService;
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

/**
 * REST controller for Member.
 *
 * @author Mason Schönherr, Jason Patrick Duffy, Robin Hackh, Tom Nguyen Dinh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/members")
public class MemberController {

    @Autowired
    MemberService memberService;

    /**
     * REST API for returning Member data of a given ID
     *
     * @param id id of the Member that should be returned
     * @param request automatically filled by browser
     * @return Member data corresponding to the given ID 404 otherwise
     */
    @Operation(summary = "Get member by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Member.class))}),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/{id}/", produces = "application/json")
    public ResponseEntity<Member> getMemberByID(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(memberService.get(id), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for adding a new Member
     *
     * @param member Member data for the new Member
     * @param request automatically filled by browser
     * @return A 201 Code and the Member data if it worked 500 otherwise
     */
    @Operation(summary = "Adds the new member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Member successfully added",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Member.class))}),
            @ApiResponse(responseCode = "500", description = "Something went wrong creating the new member", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PostMapping(path = "/", produces = "application/json")
    public ResponseEntity<Member> createMember(@RequestBody Member member, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(memberService.add(member), HttpStatus.CREATED);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for updating a new Member
     *
     * @param id ID of the Member that should be updated
     * @param member Member data for the Member update
     * @param request automatically filled by browser
     * @return A 200 Code and the Member data if it worked 404 otherwise
     */
    @Operation(summary = "Updates a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member successfully updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Member.class))}),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @PutMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<Member> updateMember(@PathVariable("id") long id, @RequestBody Member member, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try{
                return new ResponseEntity<>(memberService.update(id, member), HttpStatus.OK);
            } catch (NotFoundException e) {
                System.out.println(e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for deleting a Member
     *
     * @param id ID of the Member that should be deleted
     * @param request automatically filled by browser
     * @return A 200 Code and the Member data if it worked
     * otherwise if member not found 404
     */
    @Operation(summary = "Deletes a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content)

    })
    @DeleteMapping(path = "/{id}/", produces = "application/json")
    public ResponseEntity<HttpStatus> deleteMember(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try{
                memberService.delete(id);
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
     * REST API for deleting all Members
     *
     * @param request automatically filled by browser
     * @return A 200 Code and the Member data if it worked 500 otherwise
     */
    @Operation(summary = "Deletes all members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All members successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong deleting all members", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<HttpStatus> deleteAllMembers(HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            memberService.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
