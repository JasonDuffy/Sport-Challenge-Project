package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.DTOs.BonusDTO;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.BonusConverter;
import de.hsesslingen.scpprojekt.scp.Database.DTOs.Converter.ChallengeSportConverter;
import de.hsesslingen.scpprojekt.scp.Database.Entities.Bonus;
import de.hsesslingen.scpprojekt.scp.Database.Entities.ChallengeSport;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.BonusRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests of BonusService
 *
 * @author Jason Patrick Duffy
 */
@ActiveProfiles("test")
@SpringBootTest
public class BonusServiceTest {
    @Autowired
    BonusService bonusService;
    @Autowired
    BonusConverter bonusConverter;
    @MockBean
    ChallengeSportConverter challengeSportConverter;

    @MockBean
    BonusRepository bonusRepository;

    @MockBean
    ChallengeSportService challengeSportService;

    @Autowired
    BonusService bonusService;

    @Autowired
    BonusConverter bonusConverter;

    List<Bonus> bonusList;

    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() throws NotFoundException {
        bonusList = new ArrayList<>();

        ChallengeSport cs = new ChallengeSport();
        cs.setId(1);

        for (long i = 0; i < 10; i++){
            Bonus b = new Bonus();
            b.setId(i);
            b.setChallengeSport(cs);
            bonusList.add(b);
            when(bonusRepository.findById(i)).thenReturn(Optional.of(b));
        }

        when(bonusRepository.findAll()).thenReturn(bonusList);
        when(challengeSportConverter.convertDtoToEntity(challengeSportService.get(1L))).thenReturn(cs);

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
        ChallengeSport cs = new ChallengeSport();
        cs.setId(1);
        when(challengeSportConverter.convertDtoToEntity(challengeSportService.get(1L))).thenReturn(cs);

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
    public void updateTestSuccess() throws NotFoundException {
        ChallengeSport cs = new ChallengeSport();
        cs.setId(1);
        when(challengeSportConverter.convertDtoToEntity(challengeSportService.get(1L))).thenReturn(cs);

        bonusList.get(1).setFactor(10.5f);

        BonusDTO newBonus = bonusService.update(0L, bonusConverter.convertEntityToDto(bonusList.get(1)));

        assertEquals(newBonus.getId(), bonusList.get(0).getId());
        assertEquals(newBonus.getFactor(), bonusList.get(1).getFactor());
        assertEquals(newBonus.getChallengeSportID(), cs.getId());

        verify(bonusRepository).save(any(Bonus.class));
        verify(challengeSportService, times(2)).get(1L);
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
}
