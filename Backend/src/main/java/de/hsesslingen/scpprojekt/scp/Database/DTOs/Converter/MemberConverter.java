package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Services.ChallengeSportService;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts Member Entity to DTO and vice-versa
 *
 * @author Jason Patrick Duffy
 */
@Component
public class MemberConverter {
    @Autowired
    ImageStorageService imageStorageService;

    public MemberDTO convertEntityToDto(Member member) {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUserID(member.getId());
        memberDTO.setEmail(member.getEmail());
        memberDTO.setFirstName(member.getFirstName());
        memberDTO.setLastName(member.getLastName());
        try{
            memberDTO.setImageID(member.getImage().getId());
        } catch (NullPointerException e){
            memberDTO.setImageID(0);
        }
        memberDTO.setCommunication(member.getCommunication());
        return memberDTO;
    }

    public List<MemberDTO> convertEntityListToDtoList(List<Member> memberList){
        List<MemberDTO> memberDTOS = new ArrayList<>();

        for(Member member : memberList)
            memberDTOS.add(convertEntityToDto(member));

        return memberDTOS;
    }

    public Member convertDtoToEntity(MemberDTO memberDTO) {
        Member member = new Member();

        member.setId(memberDTO.getUserID());
        member.setFirstName(memberDTO.getFirstName());
        member.setLastName(memberDTO.getLastName());
        member.setEmail(memberDTO.getEmail());
        try{
            member.setImage(imageStorageService.get(memberDTO.getImageID()));
        } catch (NullPointerException | NotFoundException e){
            member.setImage(null);
        }
        member.setCommunication(memberDTO.getCommunication());
        return member;
    }

    public List<Member> convertDtoListToEntityList(List<MemberDTO> memberDTOS) throws NotFoundException {
        List<Member> members = new ArrayList<>();

        for(MemberDTO memberDTO : memberDTOS)
            members.add(convertDtoToEntity(memberDTO));

        return members;
    }
}
