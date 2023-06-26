package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportBonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.ChallengeSportDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportBonusConverter;
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

import java.io.File;
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

    @Autowired
    @Lazy
    ChallengeSportBonusConverter csBonusConverter;

    @MockBean
    BonusRepository bonusRepository;
    @MockBean
    ChallengeSportService challengeSportService;
    @MockBean
    ChallengeSportBonusService challengeSportBonusService;
    @MockBean
    Filler filler;
    @MockBean
    ChallengeService challengeService;
    @MockBean
    SportService sportService;
    @MockBean
    ImageStorageService imageStorageService;
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
            b.setStartDate(LocalDateTime.of(2023, 4, 1, 0, 0, 0));
            b.setEndDate(LocalDateTime.of(2023, 6, 1, 0, 0, 0));
            bonusList.add(b);
            when(bonusRepository.findById(i)).thenReturn(Optional.of(b));
        }
        when(bonusRepository.findPastBonusesByChallengeID(1)).thenReturn(bonusList);
        when(bonusRepository.findBonusesByChallengeID(1)).thenReturn(bonusList);
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
                if (b1.getId() == b.getId()) {
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
    public void addTestSuccess() throws NotFoundException, InvalidActivitiesException {
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
        when(challengeSportBonusService.add(any(ChallengeSportBonusDTO.class))).thenReturn(any(ChallengeSportBonusDTO.class));
        long [] a ={1} ;

        BonusDTO newBonus = bonusService.add(bonusConverter.convertEntityToDto(bonusList.get(0)),a);

        assertEquals(newBonus.getId(), bonusList.get(0).getId());

        verify(bonusRepository).save(any(Bonus.class));
        verify(challengeSportBonusService).add(any(ChallengeSportBonusDTO.class));
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void addTestFail() throws NotFoundException {
        when(challengeSportBonusService.add(any(ChallengeSportBonusDTO.class))).thenThrow(NotFoundException.class);
        long[] a = {1,2,3,10};
        assertThrows(NotFoundException.class, () -> {
           bonusService.add(new BonusDTO( 0L, null, null, 1.0f, null, null),a);
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

        BonusDTO newBonus = bonusService.update(0L, bonusConverter.convertEntityToDto(bonusList.get(1)), new long[]{1});

        assertEquals(newBonus.getId(), bonusList.get(0).getId());
        assertEquals(newBonus.getFactor(), bonusList.get(1).getFactor());

        verify(bonusRepository).save(any(Bonus.class));
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
    public void getMultiplierFromBonusesTest() throws NotFoundException {
        LocalDateTime currentDate = LocalDateTime.of(2023, 5, 1, 15, 0);
        float bonusfactor = 0.0f;

        for(Bonus b : bonusList){
            List <ChallengeSportBonus> csbList = csBonusConverter.convertDtoToEntityList(challengeSportBonusService.findCSBByBonusID(b.getId()));
            for (ChallengeSportBonus cs :csbList ){
                if (cs.getChallengeSport().getChallenge().getId() == 1
                        && !b.getStartDate().isAfter(currentDate)
                        && !b.getEndDate().isBefore(currentDate))
                    bonusfactor += b.getFactor();
            }

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

    /**
     * Test if correct getChallengeBonuses with past works
     */
    @Test
    public void getChallengeBonusesPastTest(){
        List<BonusDTO> bonusDTOS = bonusService.getChallengeBonuses(1,"past");
        assertEquals(bonusDTOS.get(0).getId(),bonusList.get(0).getId());
        verify(bonusRepository,times(1)).findPastBonusesByChallengeID(any(Long.class));
    }

    /**
     * Test if correct getChallengeBonuses with current works
     */
    @Test
    public void getChallengeBonusesCurrentTest(){
        bonusService.getChallengeBonuses(1,"current");
        verify(bonusRepository,times(1)).findCurrentBonusesByChallengeID(any(Long.class));
    }

    /**
     * Test if correct getChallengeBonuses with future works
     */
    @Test
    public void getChallengeBonusesFutureTest(){
        bonusService.getChallengeBonuses(1,"future");
        verify(bonusRepository,times(1)).findFutureBonusesByChallengeID(any(Long.class));
    }

    /**
     * Test if correct getChallengeBonuses default  works
     */
    @Test
    public void getChallengeBonusesTest(){
        List<BonusDTO> bonusDTOS = bonusService.getChallengeBonuses(1,"asdas");
        assertEquals(bonusDTOS.get(0).getId(),bonusList.get(0).getId());
        verify(bonusRepository,times(1)).findBonusesByChallengeID(any(Long.class));
    }


}
