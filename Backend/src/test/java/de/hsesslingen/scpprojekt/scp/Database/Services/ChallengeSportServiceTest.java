package de.hsesslingen.scpprojekt.scp.Database.Services;


import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Challenge;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 *Test of ChallengeSport Service
 *
 * @author Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class ChallengeSportServiceTest {
    @MockBean
    ChallengeSportRepository challengeSportRepository;
    @MockBean
    ChallengeService challengeService;
    @MockBean
    Filler filler;
    @MockBean
    ImageStorageService imageStorageService;
    @MockBean
    SportService sportService;

    @Autowired
    ChallengeSportConverter challengeSportConverter;
    @Autowired
    ChallengeSportService challengeSportService;
    List<ChallengeSport> CSList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        CSList = new ArrayList<>();

        Challenge cha1 = new Challenge();
        cha1.setId(1);

        Sport sp1 =  new Sport();
        sp1.setId(1);

        Sport sp2 =  new Sport();
        sp1.setId(2);

        Sport sp3 = new Sport();
        sp3.setId(3);

        ChallengeSport cs1 = new ChallengeSport();
        cs1.setId(1);
        cs1.setFactor(1);
        cs1.setChallenge(cha1);
        cs1.setSport(sp1);
        CSList.add(cs1);
        when(challengeSportRepository.findById(1L)).thenReturn(Optional.of(cs1));

        ChallengeSport cs2 = new ChallengeSport();
        cs2.setId(2);
        cs1.setFactor(1);
        cs2.setChallenge(cha1);
        cs2.setSport(sp2);
        CSList.add(cs2);
        when(challengeSportRepository.findById(2L)).thenReturn(Optional.of(cs2));

        ChallengeSport cs3 = new ChallengeSport();
        cs3.setId(3);
        cs1.setFactor(1);
        cs3.setChallenge(cha1);
        cs3.setSport(sp3);
        CSList.add(cs3);
        when(challengeSportRepository.findById(3L)).thenReturn(Optional.of(cs3));

        when(challengeSportRepository.findAll()).thenReturn(CSList);
        when(challengeService.get(1L)).thenReturn(cha1);
        when(sportService.get(1L)).thenReturn(sp1);
        when(sportService.get(2L)).thenReturn(sp2);
        when(sportService.get(3L)).thenReturn(sp3);

        when(challengeSportRepository.save(any(ChallengeSport.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given bonus class
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() {
        List<ChallengeSportDTO> CsDTOS = challengeSportService.getAll();
        for (ChallengeSportDTO cs : CsDTOS) {
            boolean test = false;
            for (ChallengeSport cs1 : CSList) {
                if (cs1.getId() == cs.getId() && cs1.getChallenge().getId() == cs.getChallengeID()) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }
        assertEquals(CSList.size(), CsDTOS.size());
        verify(challengeSportRepository).findAll();
    }

    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(ChallengeSport cs : CSList){
            assertEquals(challengeSportConverter.convertEntityToDto(cs).getId(), challengeSportService.get(cs.getId()).getId());
            verify(challengeSportRepository).findById(cs.getId());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            challengeSportService.get(15L);
        });
    }

    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestSuccess() throws NotFoundException {
        Challenge c = new Challenge();
        c.setId(1);
        when(challengeService.get(1L)).thenReturn(c);

        ChallengeSportDTO newCS = challengeSportService.add(challengeSportConverter.convertEntityToDto(CSList.get(0)));

        assertEquals(newCS.getId(), CSList.get(0).getId());
        assertEquals(newCS.getChallengeID(), c.getId());

        verify(challengeSportRepository).save(any(ChallengeSport.class));
        verify(challengeService).get(1L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
        when(challengeService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            challengeSportService.add(new ChallengeSportDTO(0,0L,0L));
        });
    }

    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess() throws NotFoundException {
        Challenge c = new Challenge();
        c.setId(1);
        when(challengeService.get(1L)).thenReturn(c);

        challengeSportService.get(1L).setFactor(10.5f);

        ChallengeSportDTO newCs = challengeSportService.update(1L, challengeSportConverter.convertEntityToDto(CSList.get(2)));

        assertEquals(newCs.getId(), CSList.get(0).getId());
        assertEquals(newCs.getFactor(), CSList.get(1).getFactor());
        assertEquals(newCs.getChallengeID(), c.getId());

        verify(challengeSportRepository).save(any(ChallengeSport.class));
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {
        when(challengeService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            challengeSportService.update(0L, challengeSportConverter.convertEntityToDto(CSList.get(0)));
        });
    }

    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        challengeSportService.delete(1L);
        verify(challengeSportRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            challengeSportService.delete(15L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        challengeSportService.deleteAll();
        verify(challengeSportRepository).deleteAll();
    }


}
