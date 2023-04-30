package de.hsesslingen.scpprojekt.scp.Database.Controller;

import java.util.Optional;

import de.hsesslingen.scpprojekt.scp.Authentication.SAML2Functions;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
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
 * @author Mason Schönherr, Jason Patrick Duffy, Robin Hackh
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    MemberRepository memberRepository;

    /**
     * REST API for returning Member data of a given ID
     *
     * @param email Email(ID) of the Member that should be returned
     * @param request automatically filled by browser
     * @return Member data corresponding to the given ID 404 otherwise
     */
    @Operation(summary = "Get member by email(ID)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Member.class))}),
            @ApiResponse(responseCode = "404", description = "Member not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @GetMapping(path ="/", produces = "application/json")
    public ResponseEntity<Member> getMemberByEmail(@RequestParam("email") String email, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Member> memberData = memberRepository.findById(email);
            if (memberData.isPresent()) {
                return new ResponseEntity<>(memberData.get(), HttpStatus.OK);
            } else {
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
            try {
                Member newMember = memberRepository.save(new Member(member.getEmail(), member.getFirstName(), member.getLastName()));
                return new ResponseEntity<>(newMember, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for updating a new Member
     *
     * @param email Email of the Member that should be updated
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
    @PutMapping(path = "/", produces = "application/json")
    public ResponseEntity<Member> updateMember(@RequestParam("email") String email, @RequestBody Member member, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            Optional<Member> memberData = memberRepository.findById(email);

            if (memberData.isPresent()) {
                Member newMember = memberData.get();
                newMember.setEmail(member.getEmail());
                newMember.setFirstName(member.getFirstName());
                newMember.setLastName(member.getLastName());
                return new ResponseEntity<>(memberRepository.save(newMember), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * REST API for deleting a Member
     *
     * @param email Email of the Member that should be deleted
     * @param request automatically filled by browser
     * @return A 200 Code and the Member data if it worked 500 otherwise
     */
    @Operation(summary = "Deleting a member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Member successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong deleting the member", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping(path = "/", produces = "application/json")
    public ResponseEntity<HttpStatus> deleteMember(@RequestParam("email") String email, HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try {
                memberRepository.deleteById(email);
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
    @Operation(summary = "Deleting all members")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All members successfully deleted"),
            @ApiResponse(responseCode = "500", description = "Something went wrong deleting all members", content = @Content),
            @ApiResponse(responseCode = "403", description = "Not logged in", content = @Content)
    })
    @DeleteMapping("/")
    public ResponseEntity<HttpStatus> deleteAllMembers(HttpServletRequest request) {
        if (SAML2Functions.isLoggedIn(request)){
            try {
                memberRepository.deleteAll();
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
