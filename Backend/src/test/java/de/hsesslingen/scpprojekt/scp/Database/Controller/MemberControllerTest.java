package de.hsesslingen.scpprojekt.scp.Database.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *  MemberController Tests
 *
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MemberControllerTest {

    @MockBean
    MemberRepository memberRepository;
    @Autowired
    private MockMvc mockMvc;

    /**
     *Test for Successfully creating a member
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addMemberSuccess()throws Exception{
        Member member = new Member();
        member.setId(1);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberRepository.save(any(Member.class))).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/members/")
                .content(new ObjectMapper().writeValueAsString(member))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(memberRepository).save(any(Member.class));
    }

    /**
     * Test for InternalServerError
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void addmemberSomethingWentWrong()throws Exception{
        Member member  = new Member();

        when(memberRepository.save(any(Member.class))).thenThrow(HttpServerErrorException.InternalServerError.class);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/members/")
                .content(new ObjectMapper().writeValueAsString(member))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isInternalServerError())
                .andReturn();

    }
    /**
     *Test for  creating a member with not been login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void addMemberNogLogin()throws Exception{
        Member member = new Member();
        member.setId(1);
        member.setFirstName("Max");
         member.setLastName("Mustermann");

        when(memberRepository.save(any(Member.class))).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/members/")
                .content(new ObjectMapper().writeValueAsString(member))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     *Test for Successfully searching a member
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void getmemberByIDSuccess() throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
         member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(memberRepository).findById(1L);
    }

    /**
     *Test for searching a non-existing member (Not Found)
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getmemberByIDNotFound() throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
         member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/4/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }
    /**
     * Test if unknown user is correctly turned away
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getmemberByIDLogout() throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
         member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }


    /**
     *Test for deleting a member with Success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void DeletememberByIdSuccess() throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().is(200))
                .andReturn();

        Mockito.verify(memberRepository).findById(1L);
        Mockito.verify(memberRepository).deleteById(1L);
    }

    /**
     *Test for deleting a non-existing member (Not Found)
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void DeletememberByIdNotFound() throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/members/3/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }
    /**
     *Test if unknown User is correctly turned away
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void DeletememberByIdLogOut() throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/members/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }
    /**
     *Test for updating a member with success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void UpdatememberSuccess()throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/members/1/")
                .content(new ObjectMapper().writeValueAsString(member))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        assertFalse(matcher.find());

        Mockito.verify(memberRepository).findById(1L);
        Mockito.verify(memberRepository).save(member);
    }
    /**
     *Test for updating a non-existing member (Not Found)
     * @throws Exception Exception by mockMvc
     */

    @Test
    @WithMockUser
    public void UpdatememberNotFound()throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/members/4/")
                .content(new ObjectMapper().writeValueAsString(member))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

    }

    /**
     * Test for turning unknown User away
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void UpdatememberLogOut()throws Exception{
        Member member = new Member();
        member.setId(1L);
        member.setFirstName("Max");
        member.setLastName("Mustermann");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/members/1/")
                .content(new ObjectMapper().writeValueAsString(member))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();

    }

}

