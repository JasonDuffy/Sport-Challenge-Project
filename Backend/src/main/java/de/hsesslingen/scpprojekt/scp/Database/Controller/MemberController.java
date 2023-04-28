package de.hsesslingen.scpprojekt.scp.Database.Controller;

import java.util.Optional;

import de.hsesslingen.scpprojekt.scp.Authentication.Controller.SAML2Controller;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Functions for the user entity
 * TODO: @Mason Kommentiere die Funktionen ordentlich - siehe SAML2Controller
 * @author Mason Schönherr, Jason Patrick Duffy
 */
@CrossOrigin(origins="http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    MemberRepository memberRepository;

    //gets member by id
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            Optional<Member> memberData = memberRepository.findById(id);
            if (memberData.isPresent()) {
                return new ResponseEntity<>(memberData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    //creates member
    @PostMapping("/")
    public ResponseEntity<Member> createMember(@RequestBody Member member, HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            try {
                Member _member = memberRepository.save(new Member(member.getFirstName(), member.getLastName()));
                return new ResponseEntity<>(_member, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    //edits member
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable("id") long id, @RequestBody Member member, HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            Optional<Member> memberData = memberRepository.findById(id);

            if (memberData.isPresent()) {
                Member _member = memberData.get();
                _member.setId(member.getId());
                _member.setFirstName(member.getFirstName());
                _member.setLastName(member.getLastName());
                return new ResponseEntity<>(memberRepository.save(_member), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    //delete member
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteMember(@PathVariable("id") long id, HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            try {
                memberRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    //deletes all members
    @DeleteMapping("/")
    public ResponseEntity<HttpStatus> deleteAllMembers(HttpServletRequest request) {
        if (SAML2Controller.isLoggedIn(request)){
            try {
                memberRepository.deleteAll();
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
