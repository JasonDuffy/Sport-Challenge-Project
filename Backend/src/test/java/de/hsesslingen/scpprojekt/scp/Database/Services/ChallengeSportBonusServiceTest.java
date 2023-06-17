package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportBonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportBonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.ChallengeSportBonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests of ChallengeSportBonusService
 *
 * @author Tom Nguyen Dinh
 */

@ActiveProfiles("test")
@SpringBootTest
public class ChallengeSportBonusServiceTest {

    @MockBean
    ChallengeSportBonusRepository challengeSportBonusRepository;
    @MockBean
    ChallengeSportService challengeSportService;
    @MockBean
    BonusService bonusService;
    @MockBean
    ChallengeService challengeService;
    @MockBean
    SportService sportService;
    @MockBean
    ImageStorageService imageStorageService;
    @Autowired
    ChallengeSportBonusService challengeSportBonusService;
    @MockBean
    Filler filler;

    @Autowired
    @Lazy
    ChallengeSportBonusConverter challengeSportBonusConverter;
    @Autowired
    @Lazy
    ChallengeConverter challengeConverter;
    @Autowired
    @Lazy
    ChallengeSportConverter csConverter;
    @Autowired
    @Lazy
    BonusConverter bonusConverter;

    List<ChallengeSportBonus> challengeSportBonusList;

    @BeforeEach
    public void setup() throws NotFoundException {
        challengeSportBonusList = new ArrayList<>();

        Image im = new Image();
        im.setId(1);

        Challenge c1 = new Challenge();
        c1.setId(1L);
        c1.setImage(im);

        Sport sport = new Sport();
        sport.setId(1L);

        ChallengeSport cs = new ChallengeSport();
        cs.setId(2L);
        cs.setChallenge(c1);
        cs.setSport(sport);

        Bonus b = new Bonus() ;
        b.setId(3);

        for (long i = 0; i < 10; i++){
            ChallengeSportBonus a = new ChallengeSportBonus();
            a.setId(i);
            a.setChallengeSport(cs);
            a.setBonus(b);
            challengeSportBonusList.add(a);
            when(challengeSportBonusRepository.findById(i)).thenReturn(Optional.of(a));
        }
        when(challengeSportBonusRepository.findAll()).thenReturn(challengeSportBonusList);
        when(imageStorageService.get(1L)).thenReturn(im);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(c1));
        when(sportService.get(1L)).thenReturn(sport);
        when(challengeSportService.get(2L)).thenReturn(csConverter.convertEntityToDto(cs));
        when(bonusService.get(3L)).thenReturn(bonusConverter.convertEntityToDto(b));

        when(challengeSportBonusRepository.save(any(ChallengeSportBonus.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given ChallengeSportBonus class
    }
    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<ChallengeSportBonusDTO> challengeSportBonusDTOList = challengeSportBonusService.getAll();

        for(ChallengeSportBonusDTO csb : challengeSportBonusDTOList){
            boolean test = false;
            for (ChallengeSportBonus csb1 : challengeSportBonusList){
                if (csb1.getId() == csb.getId() ) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }

        verify(challengeSportBonusRepository).findAll();
    }

    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(ChallengeSportBonus csb1 : challengeSportBonusList){
            assertEquals(csb1.getId(), challengeSportBonusService.get(csb1.getId()).getId());
            verify(challengeSportBonusRepository).findById(csb1.getId());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            challengeSportBonusService.get(15L);
        });
    }

    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestSuccess() throws NotFoundException {
        Challenge c1 = new Challenge();
        c1.setId(1L);

        Sport sport = new Sport();
        sport.setId(1);

        ChallengeSport cs = new ChallengeSport();
        cs.setId(2);
        cs.setSport(sport);
        cs.setChallenge(c1);

        when(challengeSportService.get(1L)).thenReturn(csConverter.convertEntityToDto(cs));

        Bonus b = new Bonus();
        b.setId(3);

        when(bonusService.get(2L)).thenReturn(bonusConverter.convertEntityToDto(b));

        ChallengeSportBonusDTO newCsb = challengeSportBonusService.add(challengeSportBonusConverter.convertEntityToDto(challengeSportBonusList.get(1)));

        assertEquals(newCsb.getId(), challengeSportBonusList.get(1).getId());
        assertEquals(newCsb.getChallengeSportID(), cs.getId());
        assertEquals(newCsb.getBonusID(), b.getId());

        verify(challengeSportBonusRepository).save(any(ChallengeSportBonus.class));
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
        when(challengeSportService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            challengeSportBonusService.add(new ChallengeSportBonusDTO(0L, 0L));
        });
    }

    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess() throws NotFoundException, InvalidActivitiesException {
        Challenge c1 = new Challenge();
        c1.setId(1L);

        Sport sport = new Sport();
        sport.setId(1);

        ChallengeSport cs = new ChallengeSport();
        cs.setId(2);
        cs.setSport(sport);
        cs.setChallenge(c1);
        when(challengeSportService.get(1L)).thenReturn(csConverter.convertEntityToDto(cs));


        ChallengeSportBonusDTO newCsb = challengeSportBonusService.update(1L,challengeSportBonusConverter.convertEntityToDto(challengeSportBonusList.get(1)));

        assertEquals(newCsb.getId(), challengeSportBonusList.get(1).getId());
        assertEquals(newCsb.getChallengeSportID(), cs.getId());

        verify(challengeSportBonusRepository).save(any(ChallengeSportBonus.class));
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {
        when(challengeSportService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            challengeSportBonusService.update(0L, challengeSportBonusConverter.convertEntityToDto(challengeSportBonusList.get(0)));
        });
    }

    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        challengeSportBonusService.delete(1L);
        verify(challengeSportBonusRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            challengeSportBonusService.delete(15L);
        });
    }


}
