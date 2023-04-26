package de.hsesslingen.scpprojekt.scp.DataBase;

import java.util.Optional;

import de.hsesslingen.scpprojekt.scp.DataBase.Entities.Member;
import de.hsesslingen.scpprojekt.scp.DataBase.Repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    MemberRepository memberRepository;

    //gets member by id
    @GetMapping("/members/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable("id") long id) {
        Optional<Member> memberData = memberRepository.findById(id);

        if (memberData.isPresent()) {
            return new ResponseEntity<>(memberData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //creates member
    @PostMapping("/members")
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        try {
            Member _member = memberRepository
                    .save(new Member(/*member.getFirstName(), member.getLastName(), false*/));
            return new ResponseEntity<>(_member, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //edits member
    @PutMapping("/members/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable("id") long id, @RequestBody Member member) {
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
    }

    //delete member
    @DeleteMapping("/members/{id}")
    public ResponseEntity<HttpStatus> deleteMember(@PathVariable("id") long id) {
        try {
            memberRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //deletes all members
    @DeleteMapping("/members")
    public ResponseEntity<HttpStatus> deleteAllMembers() {
        try {
            memberRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
