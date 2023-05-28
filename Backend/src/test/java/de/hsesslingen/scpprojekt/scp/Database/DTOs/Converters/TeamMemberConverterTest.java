package de.hsesslingen.scpprojekt.scp.Database.DTOs.Converters;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.TeamMemberConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.TeamMemberDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.TeamRepository;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamMemberService;
import de.hsesslingen.scpprojekt.scp.Database.Services.TeamService;
import de.hsesslingen.scpprojekt.scp.Exceptions.AlreadyExistsException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.processing.Filer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if the TeamMemberConverter is correct
 *
 * @author  Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class TeamMemberConverterTest {
    @Autowired
    TeamMemberConverter teamMemberConverter;
    @MockBean
    TeamMemberService teamMemberService;
    List<TeamMember> teamMemberList;
    List<TeamMemberDTO> teamMemberDTOList ;
    TeamMemberDTO teamMemberDTO;
    TeamMember teamMember;


    @BeforeEach
    public void setup() throws AlreadyExistsException, NotFoundException {

        Member member = new Member();
        member.setId(1);
        Team team = new Team();
        team.setId(2);

        teamMember = new TeamMember();
        teamMember.setId(3);
        teamMember.setTeam(team);
        teamMember.setMember(member);

        teamMemberList = new ArrayList<>();
        teamMemberList.add(teamMember);

        teamMemberDTO = teamMemberConverter.convertEntityToDto(teamMember);
        teamMemberDTOList = new ArrayList<>();
        teamMemberDTOList.add(teamMemberDTO);
    }

    @Test
    public void convertEntityToDtoTest(){
        TeamMemberDTO a = teamMemberConverter.convertEntityToDto(teamMember);
        assertEquals(1,a.getMemberID());
        assertEquals(2,a.getTeamID());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertEntityListToDtoListTEST(){
        List<TeamMemberDTO> a = teamMemberConverter.convertEntityListToDtoList(teamMemberList);
        assertEquals(1,a.get(0).getMemberID());
        assertEquals(2,a.get(0).getTeamID());
        assertEquals(3,a.get(0).getId());
    }

    @Test
    public void convertDtoToEntityTEST() throws NotFoundException {
        TeamMember a = teamMemberConverter.convertDtoToEntity(teamMemberDTO);
        assertEquals(1,a.getMember().getId());
        assertEquals(2,a.getTeam().getId());
        assertEquals(3,a.getId());
    }

    @Test
    public void convertDtoListToEntityListTest() throws NotFoundException {
        List<TeamMember> a = teamMemberConverter.convertDtoListToEntityList(teamMemberDTOList);
        assertEquals(1,a.get(0).getMember().getId());
        assertEquals(2,a.get(0).getTeam().getId());
        assertEquals(3,a.get(0).getId());
    }
}
