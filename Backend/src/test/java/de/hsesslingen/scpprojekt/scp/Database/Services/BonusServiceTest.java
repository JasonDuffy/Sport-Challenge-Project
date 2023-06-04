package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.*;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
import de.hsesslingen.scpprojekt.scp.Exceptions.InvalidActivitiesException;
import de.hsesslingen.scpprojekt.scp.Exceptions.NotFoundException;
import de.hsesslingen.scpprojekt.scp.Mail.Services.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests of BonusService
 *
 * @author Jason Patrick Duffy, Tom Nguyen Dinh
 */
@ActiveProfiles("test")
@SpringBootTest
public class BonusServiceTest {
    @Autowired
    BonusService bonusService;
    @Autowired
    BonusConverter bonusConverter;
    @Autowired
    @Lazy
    ChallengeSportConverter csConverter;
    @Autowired
    @Lazy
    ChallengeConverter challengeConverter;

    @MockBean
    BonusRepository bonusRepository;
    @MockBean
    ChallengeSportService challengeSportService;
    @MockBean
    ChallengeSportBonusService challengeSportBonusService;
    @MockBean
    ChallengeService challengeService;
    @MockBean
    SportService sportService;
    @MockBean
    ImageStorageService imageStorageService;
    @MockBean
    Filler filler;
    @MockBean
    EmailService emailService; // Mock so no emails are sent

    List<Bonus> bonusList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        bonusList = new ArrayList<>();

        Challenge c1 = new Challenge();
        c1.setId(1L);

        Sport sport = new Sport();
        sport.setId(1);

        ChallengeSport cs = new ChallengeSport();
        cs.setId(1L);
        cs.setChallenge(c1);
        cs.setSport(sport);

        for (long i = 0; i < 10; i++){
            Bonus b = new Bonus();
            b.setId(i);
            b.setChallengeSport(cs);
            b.setStartDate(LocalDateTime.of(2023, 4, 1, 0, 0, 0));
            b.setEndDate(LocalDateTime.of(2023, 6, 1, 0, 0, 0));
            bonusList.add(b);
            when(bonusRepository.findById(i)).thenReturn(Optional.of(b));
        }

        when(bonusRepository.findAll()).thenReturn(bonusList);
        when(challengeSportService.get(1L)).thenReturn(csConverter.convertEntityToDto(cs));

        when(bonusRepository.save(any(Bonus.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given bonus class
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() throws NotFoundException {
        List<BonusDTO> bonuses = bonusService.getAll();

        for(BonusDTO b : bonuses){
            boolean test = false;
            for (Bonus b1 : bonusList){
                if (b1.getId() == b.getId() && b1.getChallengeSport().getId() == b.getChallengeSportID()) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }

        assertEquals(bonusList.size(), bonuses.size());

        verify(bonusRepository).findAll();
    }

    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(Bonus b : bonusList){
            assertEquals(bonusConverter.convertEntityToDto(b).getId(), bonusService.get(b.getId()).getId());
            verify(bonusRepository).findById(b.getId());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
           bonusService.get(15L);
        });
    }

    /**
     * Test if add works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestSuccess() throws NotFoundException {
        Image im = new Image();
        im.setId(1);

        Challenge c1 = new Challenge();
        c1.setId(1L);
        c1.setImage(im);

        Sport sport = new Sport();
        sport.setId(1);

        ChallengeSport cs = new ChallengeSport();
        cs.setId(1);
        cs.setSport(sport);
        cs.setChallenge(c1);

        when(sportService.get(1L)).thenReturn(sport);
        when(imageStorageService.get(1L)).thenReturn(im);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(c1));
        when(challengeSportService.get(1L)).thenReturn(csConverter.convertEntityToDto(cs));

        BonusDTO newBonus = bonusService.add(bonusConverter.convertEntityToDto(bonusList.get(0)));

        assertEquals(newBonus.getId(), bonusList.get(0).getId());
        assertEquals(newBonus.getChallengeSportID(), cs.getId());

        verify(bonusRepository).save(any(Bonus.class));
        verify(challengeSportService).get(1L);
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
        when(challengeSportService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
           bonusService.add(new BonusDTO(0L, 0L, null, null, 1.0f, null, null));
        });
    }

    /**
     * Test is update works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestSuccess() throws NotFoundException, InvalidActivitiesException {
        Image im = new Image();
        im.setId(1);

        Challenge c1 = new Challenge();
        c1.setId(1L);
        c1.setImage(im);

        Sport sport = new Sport();
        sport.setId(1);

        ChallengeSport cs = new ChallengeSport();
        cs.setId(1);
        cs.setSport(sport);
        cs.setChallenge(c1);
        when(sportService.get(1L)).thenReturn(sport);
        when(imageStorageService.get(1L)).thenReturn(im);
        when(challengeService.get(1L)).thenReturn(challengeConverter.convertEntityToDto(c1));
        when(challengeSportService.get(1L)).thenReturn(csConverter.convertEntityToDto(cs));
        bonusList.get(1).setFactor(10.5f);

        BonusDTO newBonus = bonusService.update(0L, bonusConverter.convertEntityToDto(bonusList.get(1)));

        assertEquals(newBonus.getId(), bonusList.get(0).getId());
        assertEquals(newBonus.getFactor(), bonusList.get(1).getFactor());
        assertEquals(newBonus.getChallengeSportID(), cs.getId());

        verify(bonusRepository).save(any(Bonus.class));
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {
        when(challengeSportService.get(any(Long.class))).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            bonusService.update(0L, bonusConverter.convertEntityToDto(bonusList.get(0)));
        });
    }

    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        bonusService.delete(1L);
        verify(bonusRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            bonusService.delete(15L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        bonusService.deleteAll();
        verify(bonusRepository).deleteAll();
    }

     /**
     * Test if multiplier is correctly calculated
     */
    @Test
    public void getMultiplierFromBonusesTest(){
        LocalDateTime currentDate = LocalDateTime.of(2023, 5, 1, 15, 0);
        float bonusfactor = 0.0f;

        for(Bonus b : bonusList){
            if (b.getChallengeSport().getChallenge().getId() == 1
                    && !b.getStartDate().isAfter(currentDate)
                    && !b.getEndDate().isBefore(currentDate))
                bonusfactor += b.getFactor();
        }

        if (bonusfactor == 0.0f)
            bonusfactor = 1.0f;

        assertEquals(bonusService.getMultiplierFromBonuses(bonusList, currentDate), bonusfactor);
    }

    /**
     * Test if multiplier is correctly set to 1 if no bonuses are given
     */
    @Test
    public void getMultiplierFromBonusesEmpty(){
        LocalDateTime currentDate = LocalDateTime.of(2023, 5, 1, 15, 0);
        assertEquals(bonusService.getMultiplierFromBonuses(new ArrayList<>(), currentDate), 1.0f);
    }
}
