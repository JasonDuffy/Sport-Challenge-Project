package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converters;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.MemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.MemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Services.MemberService;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the MemberConverter is correct
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class MemberConverterTest {
    @MockBean
    MemberService memberService;
    @Autowired
    MemberConverter memberConverter;

    List<Member> memberList ;
    List<MemberDTO> memberDTOList ;
    MemberDTO memberDTO;
    Member member;


    @BeforeEach
    public void setup() throws AlreadyExistsException, NotFoundException {
        Image image = new Image();
        image.setId(1L);

        member = new Member();
        member.setId(2);
        member.setImage(image);
        memberList = new ArrayList<>();
        memberList.add(member);

        memberDTO = memberConverter.convertEntityToDto(member);
        memberDTOList = new ArrayList<>();
        memberDTOList.add(memberDTO);
    }

    @Test
    public void convertEntityToDtoTest(){
        MemberDTO a = memberConverter.convertEntityToDto(member);
        assertEquals(1,a.getImageID());
        assertEquals(2,a.getUserID());

    }

    @Test
    public void convertEntityListToDtoListTEST(){
        List<MemberDTO> a = memberConverter.convertEntityListToDtoList(memberList);
        assertEquals(2,a.get(0).getUserID());
        assertEquals(1,a.get(0).getImageID());
    }

    @Test
    public void convertDtoToEntityTEST() {
        Member a = memberConverter.convertDtoToEntity(memberDTO);
        assertEquals(2,a.getId());
    }

    @Test
    public void convertDtoListToEntityListTest() throws NotFoundException {
        List<Member> a = memberConverter.convertDtoListToEntityList(memberDTOList);
        assertEquals(2,a.get(0).getId());
    }
}
