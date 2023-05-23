package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeRepository;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests of Activity Service
 *
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeServiceTest {
    @MockBean
    ChallengeRepository challengeRepository;
    @MockBean
    ImageStorageService imageStorageService;
    @MockBean
    ChallengeSportRepository challengeSportRepository;
    @MockBean
    SportService sportService;
    @MockBean
    Filler filler;
    @Autowired
    ChallengeService challengeService;
    @Autowired
    ChallengeConverter challengeConverter;

    List<Challenge> challengeList;
    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        challengeList = new ArrayList<>();

        Image image = new Image();
        image.setId(1);

        Sport sport = new Sport();
        sport.setId(1);

        for (long i = 0; i < 10; i++){
            Challenge c = new Challenge();
            c.setId(i);
            c.setImage(image);
            challengeList.add(c);
            when(challengeRepository.findById(i)).thenReturn(Optional.of(c));
        }

        when(challengeRepository.findAll()).thenReturn(challengeList);
        when(imageStorageService.get(1L)).thenReturn(image);
        when(sportService.get(1L)).thenReturn(sport);

        when(challengeRepository.save(any(Challenge.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given challenge class

    }
    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<Challenge> challenges = challengeConverter.convertDtoListToEntityList(challengeService.getAll());
        for(Challenge c : challenges){
            boolean test = false;
            for (Challenge c1 : challengeList){
                if (c1.getId() == c.getId() && c1.getImage().getId() == c.getImage().getId()) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }
        assertEquals(challengeList.size(), challenges.size());
        verify(challengeRepository).findAll();
    }
    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(Challenge c : challengeList){
            assertEquals(challengeConverter.convertEntityToDto(c).getId(), challengeService.get(c.getId()).getId());
            verify(challengeRepository).findById(c.getId());
        }
    }
    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            challengeService.get(15L);
        });
    }

    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown

     */
    @Test
    public void addTestSuccess() throws NotFoundException {

        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());

        ChallengeDTO newC = challengeService.add(file,new long[]{1L},new float[]{10F},challengeConverter.convertEntityToDto(challengeList.get(0)));

        verify(challengeRepository).save(any(Challenge.class));

    }
    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
       when(sportService.get(any(Long.class))).thenThrow(NotFoundException.class);

        MockMultipartFile file = new MockMultipartFile("file", "file.png", String.valueOf(MediaType.IMAGE_PNG), "Test123".getBytes());

        assertThrows(NotFoundException.class, () -> {
            challengeService.add(file,new long[]{1L},new float[]{10F},challengeConverter.convertEntityToDto(challengeList.get(0)));
        });
    }
    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     *
     */


    @Test
    public void updateTestSuccess() throws NotFoundException {

        challengeList.get(1).setName("name");

        ChallengeDTO newC = challengeService.update(1,0L, challengeConverter.convertEntityToDto(challengeList.get(1)));

        assertEquals(newC.getId(), challengeList.get(0).getId());
        assertEquals(newC.getName(), challengeList.get(0).getName());

        verify(challengeRepository).save(any(Challenge.class));
    }


    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {
        assertThrows(NotFoundException.class, () -> {
            challengeService.update(1,20L, challengeConverter.convertEntityToDto(challengeList.get(0)));
        });
    }
    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        challengeService.delete(1L);
        verify(challengeRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            challengeService.delete(15L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        challengeService.deleteAll();
        verify(challengeRepository).deleteAll();
    }
}
