package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ActivityDTO;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Activity;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Image;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Member;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ImageRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * tests of ImageStorage Service
 *
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ImageServiceTest {
    @Autowired
    ImageStorageService imageStorageService;
    @MockBean
    ImageRepository imageRepository;
    @MockBean
    Filler filler;
    List<Image> imageList;
    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup(){
        imageList = new ArrayList<>();
        for (long i = 0; i < 10; i++){
            Image ima = new Image();
            ima.setId(i);
            imageList.add(ima);
            when(imageRepository.findById(i)).thenReturn(Optional.of(ima));
        }
        when(imageRepository.findAll()).thenReturn(imageList);

        when(imageRepository.save(any(Image.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given image class
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<Image> images = imageStorageService.getAll();

        for(Image i : images){
            boolean test = false;
            for (Image i1 : imageList){
                if (i1.getId() == i.getId()) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }
        assertEquals(imageList.size(), images.size());
        verify(imageRepository).findAll();
    }
    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(Image i : imageList){
            assertEquals(i.getId(), imageStorageService.get(i.getId()).getId());
            verify(imageRepository).findById(i.getId());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            imageStorageService.get(15L);
        });
    }

    /**
     * Test if add works correctly
     * @throws IOException Should never be thrown
     */

    /*
    @Test
    public void addTestSuccess() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "file.jpg","image/jpeg" , "Test123".getBytes());
        imageStorageService.store(file);

        verify(imageRepository).save(any(Image.class));

    }
*/
    /**
     * Test if exception is correctly thrown
     *
     */
    /*
    @Test
    public void addTestFail(){

        MockMultipartFile file = new MockMultipartFile("file", "file.jpg","image/jpeg" , "Test123".getBytes());

        assertThrows(IOException.class, () -> {
            imageStorageService.store(file);
        });
    }
    @Test
    public void updateTestSuccess() throws NotFoundException, IOException {
        MockMultipartFile file = new MockMultipartFile("file", "file.jpg","image/jpeg" , "Test123".getBytes());
        Image image = new Image();
        image.setId(1);
        imageStorageService.update(1,file);
    }

    /**
     * Test if exception is correctly thrown
     *
     */

    @Test
    public void updateTestFail() {
        MockMultipartFile file = new MockMultipartFile("file", "file.jpg","image/jpeg" , "Test123".getBytes());

        assertThrows(IOException.class, () -> {
            imageStorageService.update(0L, file);
        });
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Not found Image
     */
    /*
    @Test
    public void updateTestFailNotfound() throws NotFoundException, IOException {
        MockMultipartFile file = new MockMultipartFile("file", "file.jpg","image/jpeg" , "Test123".getBytes());
        imageStorageService.update(20L,file);
    }
*/
    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        imageStorageService.delete(1L);
        verify(imageRepository).deleteById(1L);
    }

    /**
     * Test if exception    is correctly thrown
     */
    @Test
    public void deleteTestFail() {
        assertThrows(NotFoundException.class, () -> {
            imageStorageService.delete(20L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        imageStorageService.deleteAll();
        verify(imageRepository).deleteAll();
    }
}
