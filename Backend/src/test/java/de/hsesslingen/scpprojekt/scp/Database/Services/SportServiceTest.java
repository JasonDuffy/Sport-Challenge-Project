package de.hsesslingen.scpprojekt.scp.Database.Services;

import de.hsesslingen.scpprojekt.scp.Database.Entities.Sport;
import de.hsesslingen.scpprojekt.scp.Database.Filler.Filler;
import de.hsesslingen.scpprojekt.scp.Database.Repositories.SportRepository;
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
import static org.mockito.Mockito.verify;

/**
 * Tests of BonusService
 *
 * @author Tom Nguyen Dinh
 */

@ActiveProfiles("test")
@SpringBootTest
public class SportServiceTest {
    @MockBean
    SportRepository sportRepository;
    @MockBean
    Filler filler;
    @Autowired
    SportService sportService;

    List<Sport> sportList;
    /**
     * Sets up tests
     */
    @BeforeEach
    public void setup() {
        sportList = new ArrayList<>();

        for (long i = 0; i < 10; i++){
            Sport sp = new Sport();
            sp.setId(i);;
            sportList.add(sp);
            when(sportRepository.findById(i)).thenReturn(Optional.of(sp));
        }
        when(sportRepository.findAll()).thenReturn(sportList);
        when(sportRepository.save(any(Sport.class))).then(AdditionalAnswers.returnsFirstArg()); //Return given bonus class
    }

    /**
     * Test if getAll works correctly
     */
    @Test
    public void getAllTest() {
        List<Sport> sports = sportService.getAll();

        for(Sport sp : sports){
            boolean test = false;
            for (Sport sp1 : sportList){
                if (sp1.getId() == sp.getId()) {
                    test = true;
                    break;
                }
            }
            assertTrue(test);
        }
        assertEquals(sportList.size(), sports.size());
        verify(sportRepository).findAll();
    }

    /**
     * Test if get works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void getTestSuccess() throws NotFoundException {
        for(Sport sp : sportList){
            assertEquals(sp.getId(), sportService.get(sp.getId()).getId());
            verify(sportRepository).findById(sp.getId());
        }
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void getTestFail(){
        assertThrows(NotFoundException.class, () -> {
            sportService.get(15L);
        });
    }

    /**
     * Test if add works correctly
     */
    @Test
    public void addTestSuccess() {

        Sport newSport = sportService.add(sportList.get(0));

        assertEquals(newSport.getId(), sportList.get(0).getId());

        verify(sportRepository).save(any(Sport.class));
    }

    /**
     * Test is update works correctly
     *
     */
    @Test
    public void updateTestSuccess() throws NotFoundException{

        sportList.get(1).setFactor(10.5f);

        Sport newSport = sportService.update(0L, sportList.get(1));

        assertEquals(newSport.getId(), sportList.get(0).getId());
        assertEquals(newSport.getFactor(), sportList.get(1).getFactor());

        verify(sportRepository).save(any(Sport.class));
    }

    /**
     * Test if exception is correctly thrown
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void updateTestFail() throws NotFoundException {

        when(sportRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            sportService.update(10L, sportList.get(1));
        });


    }

    /**
     * Test if delete works correctly
     * @throws NotFoundException Should never be thrown
     */
    @Test
    public void deleteTestSuccess() throws NotFoundException {
        sportService.delete(1L);
        verify(sportRepository).deleteById(1L);
    }

    /**
     * Test if exception is correctly thrown
     */
    @Test
    public void deleteTestFail(){
        assertThrows(NotFoundException.class, () -> {
            sportService.delete(15L);
        });
    }

    /**
     * Test if deleteAll works correctly
     */
    @Test
    public void deleteAllTest(){
        sportService.deleteAll();
        verify(sportRepository).deleteAll();
    }
}
