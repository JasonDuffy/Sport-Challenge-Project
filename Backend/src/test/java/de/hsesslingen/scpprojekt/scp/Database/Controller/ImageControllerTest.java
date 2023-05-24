package de.hsesslingen.scpprojekt.scp.Database.Controller;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Services.ImageStorageService;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ImageStorageService imageservice;


    /**
     * Test if all images are returned correctly
     *
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getALLImagesTestSuccess() throws Exception {
        Image i1 = new Image();
        i1.setId(1);
        Image i2 = new Image();
        i2.setId(2);
        List<Image> iList = new ArrayList<>();
        iList.add(i1);
        iList.add(i2);

        when(imageservice.getAll()).thenReturn(iList);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/images/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String content = res.getResponse().getContentAsString();

        Pattern pattern = Pattern.compile("\\{\"id\":(\\d),");
        Matcher matcher = pattern.matcher(content);

        matcher.find();
        assertEquals(matcher.group(1), "1");
        matcher.find();
        assertEquals(matcher.group(1), "2");
        assertFalse(matcher.find());

        Mockito.verify(imageservice).getAll();
    }

    /**
     * Test if unknown user is correctly turned away
     * @throws Exception by mockMvc
     */

    @Test
    @WithAnonymousUser
    public void getAllImagesTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/images/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }
    /**
     * Test  image by ID are returned correctly
     *
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getImageByIDTestSuccess() throws Exception {
        Image i1 = new Image();
        i1.setId(1);

        when(imageservice.get(1L)).thenReturn(i1);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/images/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Mockito.verify(imageservice).get(1L);
    }
    /**
     * Test if 404 is returned when no image are found
     *
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void getImageByIDNotFound() throws Exception {
        Image i1 = new Image();
        i1.setId(1);

        when(imageservice.get(1L)).thenThrow(NotFoundException.class);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/images/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();
    }

    /**
     * Test if unknown user is correctly turned away
     *
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void getImageByIDNotLoggedIn() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders
                .get("/images/1/").accept(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if image is uploaded correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void storeImageTestSuccess() throws Exception {
        Image i1 = new Image();
        i1.setId(1);

        when(imageservice.store(any(MockMultipartFile.class))).thenReturn(i1);
        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());

        RequestBuilder request =
                MockMvcRequestBuilders.multipart("/images/")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(imageservice).store(any(MultipartFile.class));
    }

    /**
     * Test if unknown user tries to upload
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void storeImageAnonymous() throws Exception {
        Image i1 = new Image();
        i1.setId(1);
        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());

        RequestBuilder request =
                MockMvcRequestBuilders.multipart("/images/")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if Image is deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteImageTestSuccess() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/images/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(imageservice).delete(1L);
    }

    /**
     * Test if 404 is returned when image is not found
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteActivityTestNotFound() throws Exception {
        doThrow(NotFoundException.class).when(imageservice).delete(1L);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/images/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andReturn();

        Mockito.verify(imageservice).delete(1L);
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteActivityTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/images/1/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test if all images are deleted correctly
     * @throws Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void deleteAllActivitiesTestSuccess() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/images/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(imageservice).deleteAll();
    }

    /**
     * Test if 403 is returned when user is not logged in
     * @throws Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void deleteAllActivitiesTestNotLoggedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/images/").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult res = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
    }

    /**
     * Test for updating an image success
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateActivityTestSuccess() throws Exception {
        Image i1 = new Image();
        i1.setId(1);

        when(imageservice.update(any(long.class), any(MultipartFile.class))).thenReturn(i1);

        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/images/1/");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        mockMvc.perform(builder
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andReturn();

        Mockito.verify(imageservice).update(any(Long.class),any(MultipartFile.class));
    }

    /**
     * Test for updating an image not found
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithMockUser
    public void updateActivityNotFound() throws Exception {

        when(imageservice.update(any(long.class), any(MultipartFile.class))).thenThrow(NotFoundException.class);

        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/images/1/");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        mockMvc.perform(builder
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    /**
     * Test for updating an image not login
     * @throws Exception Exception by mockMvc
     */
    @Test
    @WithAnonymousUser
    public void updateActivityNotLoggedIn() throws Exception {


        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/images/1/");
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        mockMvc.perform(builder
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }
}